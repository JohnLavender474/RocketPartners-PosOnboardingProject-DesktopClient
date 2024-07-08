package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.commons.model.*;
import com.rocketpartners.onboarding.commons.utils.UtilMethods;
import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.constant.ConstVals;
import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.IPosEventManager;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import com.rocketpartners.onboarding.possystem.service.DiscountService;
import com.rocketpartners.onboarding.possystem.service.ItemService;
import com.rocketpartners.onboarding.possystem.service.TransactionService;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for POS system. This class is responsible for handling POS events and managing transactions.
 */
@ToString
public class PosComponent implements IComponent, IPosEventManager {

    private final ItemBookLoaderComponent itemBookLoaderComponent;

    private final TransactionService transactionService;
    private final ItemService itemService;
    private final DiscountService discountService;

    private final Map<PosEventType, List<PosEvent>> events;
    private final Set<IComponent> childComponents;
    private final Set<IPosEventListener> posEventListeners;

    @Getter
    private boolean on;
    @Getter
    private PosSystem posSystem;
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private Transaction transaction;
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private TransactionState transactionState;
    @Getter
    private int transactionNumber;

    private boolean shuttingDown;

    /**
     * Constructor that accepts a transaction service, an item service, and a POS journal component. The transaction
     * state is set to NOT_STARTED and the transaction number is set to 1.
     *
     * @param transactionService The transaction service.
     * @param itemService        The item service.
     */
    public PosComponent(@NonNull ItemBookLoaderComponent itemBookLoaderComponent,
                        @NonNull TransactionService transactionService, @NonNull ItemService itemService,
                        @NonNull DiscountService discountService) {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Creating POS component");
        }

        this.itemBookLoaderComponent = itemBookLoaderComponent;
        this.transactionService = transactionService;
        this.itemService = itemService;
        this.discountService = discountService;

        events = new EnumMap<>(PosEventType.class);
        childComponents = new LinkedHashSet<>();
        posEventListeners = new LinkedHashSet<>();
        transactionState = TransactionState.NOT_STARTED;
        transactionNumber = 1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this implementation, this {@link PosComponent} instance listens and acts on the event immediately. However,
     * {@link IPosEventListener} instances do not receive the event until the {@link #update()} method call is made.
     *
     * @param event The event to dispatch.
     */
    @Override
    public void dispatchPosEvent(@NonNull PosEvent event) {
        handlePosEvent(event);
        PosEventType type = event.getType();
        List<PosEvent> eventsOfType = events.getOrDefault(type, new ArrayList<>());
        eventsOfType.add(event);
        events.put(type, eventsOfType);
    }

    private void handlePosEvent(@NonNull PosEvent event) {
        switch (event.getType()) {
            case REQUEST_START_TRANSACTION -> handleRequestStartTransaction(event);
            case REQUEST_OPEN_SCANNER -> handleRequestOpenScanner();
            case REQUEST_OPEN_POLE_DISPLAY -> handleRequestOpenPoleDisplay();
            case REQUEST_OPEN_DISCOUNTS -> handleRequestOpenDiscounts();
            case REQUEST_ADD_ITEM -> handleRequestAddItem(event);
            case REQUEST_REMOVE_ITEM -> handleRequestRemoveItem(event);
            case REQUEST_UPDATE_QUICK_ITEMS -> handleRequestUpdateQuickItems();
            case REQUEST_VOID_LINE_ITEMS -> handleRequestVoidLineItems(event);
            case REQUEST_VOID_TRANSACTION -> handleRequestVoidTransaction();
            case REQUEST_START_PAY_WITH_CARD_PROCESS -> handleRequestStartPayWithCardProcess();
            case REQUEST_START_PAY_WITH_CASH_PROCESS -> handleRequestStartPayWithCashProcesss();
            case REQUEST_ENTER_CARD_NUMBER -> handleRequestEnterCardNumber(event);
            case REQUEST_INSERT_CASH -> handleRequestInsertCash(event);
            case REQUEST_CANCEL_PAYMENT -> handleRequestCancelPayment();
            case REQUEST_COMPLETE_TRANSACTION -> handleRequestCompleteTransaction();
            case REQUEST_RESET_POS -> resetPos();
        }
    }

    private void handleRequestStartTransaction(@NonNull PosEvent event) {
        if (transactionState != TransactionState.NOT_STARTED) {
            String error = "Cannot start transaction when transaction state is not NOT_STARTED. Current transaction " +
                    "state: " + transactionState;
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        startTransaction(event);
    }

    private void handleRequestOpenScanner() {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Request to open scanner");
        }
        dispatchPosEvent(new PosEvent(PosEventType.DO_OPEN_SCANNER));
    }

    private void handleRequestOpenPoleDisplay() {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Request to open pole display");
        }
        dispatchPosEvent(new PosEvent(PosEventType.DO_OPEN_POLE_DISPLAY));
    }

    private void handleRequestOpenDiscounts() {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Request to open discounts");
        }
        dispatchPosEvent(new PosEvent(PosEventType.DO_OPEN_DISCOUNTS));
    }

    private void handleRequestAddItem(@NonNull PosEvent event) {
        if (transactionState == TransactionState.NOT_STARTED) {
            if (Application.DEBUG) {
                System.out.println("[PosComponent] Request to add item deferred until after transaction " + "starts");
            }
            dispatchPosEvent(new PosEvent(PosEventType.REQUEST_START_TRANSACTION, event.getCopyOfProps()));
            return;
        }

        if (transactionState != TransactionState.SCANNING_IN_PROGRESS || !isTransactionEditable()) {
            if (transactionState != TransactionState.SCANNING_IN_PROGRESS) {
                String error = "Cannot add item to transaction when transaction state is not SCANNING_IN_PROGRESS. " +
                        "Current transaction state: " + transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            }

            if (!isTransactionEditable()) {
                String error = "Cannot add item to transaction when transaction is not editable. Current " +
                        "transaction state: " + transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            }

            return;
        }

        String itemUpc = event.getProperty(ConstKeys.ITEM_UPC, String.class);

        if (itemUpc == null) {
            String error = "Cannot add item to transaction because item UPC is null";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        if (itemUpc.isBlank()) {
            String error = "Cannot add item to transaction because item UPC is blank";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        if (itemService.itemExists(itemUpc) && transactionService.addItemToTransaction(transaction, itemUpc)) {
            TransactionDto transactionDto = getTransactionDto();

            Item item = itemService.getItemByUpc(itemUpc);
            ItemDto itemDto = ItemDto.from(item);

            dispatchPosEvent(new PosEvent(PosEventType.ITEM_ADDED,
                    Map.of(ConstKeys.ITEM_DTO, itemDto, ConstKeys.TRANSACTION_DTO, transactionDto)));
            dispatchPosEvent(new PosEvent(PosEventType.LOG,
                    Map.of(ConstKeys.MESSAGE, "Item " + itemUpc + " added to transaction.")));

            if (Application.DEBUG) {
                System.out.println("[PosComponent] Item added to transaction: " + itemUpc);
            }
        } else {
            String error = "Cannot add item to transaction because item with UPC [ " + itemUpc + " ] does not exist";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
        }
    }

    private void handleRequestRemoveItem(@NonNull PosEvent event) {
        if (transactionState != TransactionState.SCANNING_IN_PROGRESS || !isTransactionEditable()) {
            if (transactionState != TransactionState.SCANNING_IN_PROGRESS) {
                String error = "Cannot remove item from transaction when transaction state is not " +
                        "SCANNING_IN_PROGRESS. " + "Current transaction state: " + transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            }

            if (!isTransactionEditable()) {
                String error = "Cannot remove item from transaction when transaction is not editable. Current " +
                        "transaction state: " + transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            }

            return;
        }

        String itemUpc = event.getProperty(ConstKeys.ITEM_UPC, String.class);
        if (itemUpc == null) {
            String error = "Cannot remove item from transaction because item UPC is null";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        if (itemService.itemExists(itemUpc) && transactionService.removeItemFromTransaction(transaction, itemUpc)) {
            TransactionDto transactionDto = getTransactionDto();

            Item item = itemService.getItemByUpc(itemUpc);
            ItemDto itemDto = ItemDto.from(item);

            dispatchPosEvent(new PosEvent(PosEventType.ITEM_REMOVED,
                    Map.of(ConstKeys.ITEM_DTO, itemDto, ConstKeys.TRANSACTION_DTO, transactionDto)));
            dispatchPosEvent(new PosEvent(PosEventType.LOG,
                    Map.of(ConstKeys.MESSAGE, "Item " + itemUpc + " removed from transaction.")));
        } else {
            String error = "Cannot remove item from transaction because item with UPC " + itemUpc + " does not exist";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
        }
    }

    private void handleRequestVoidLineItems(@NonNull PosEvent event) {
        if (transactionState == TransactionState.NOT_STARTED || !isTransactionEditable()) {
            if (transactionState == TransactionState.NOT_STARTED) {
                String error =
                        "Cannot void line items when transaction is not in progress. Current " + "transaction state: " +
                                transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            }

            if (!isTransactionEditable()) {
                String error =
                        "Cannot void line items when transaction is not editable. Current " + "transaction state: " +
                                transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            }

            return;
        }

        Collection<String> itemUpcs = (Collection<String>) event.getProperty(ConstKeys.ITEM_UPCS);
        if (itemUpcs == null) {
            String error = "Cannot void line items because item UPCs is null";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        itemUpcs.forEach(it -> transactionService.voidLineItemInTransaction(transaction, it));

        TransactionDto transactionDto = getTransactionDto();
        dispatchPosEvent(
                new PosEvent(PosEventType.LINE_ITEMS_VOIDED, Map.of(ConstKeys.TRANSACTION_DTO, transactionDto)));
        dispatchPosEvent(new PosEvent(PosEventType.LOG,
                Map.of(ConstKeys.MESSAGE, "Line items voided from transaction: " + itemUpcs)));
    }

    private void handleRequestVoidTransaction() {
        if (transactionState == TransactionState.NOT_STARTED || !isTransactionEditable()) {
            if (transactionState == TransactionState.NOT_STARTED) {
                String error = "Cannot void transaction when transaction is not in progress. Current " +
                        "transaction state: " + transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            }

            if (!isTransactionEditable()) {
                String error =
                        "Cannot void transaction when transaction is not editable. Current " + "transaction state: " +
                                transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            }

            return;
        }

        voidTransaction();
    }

    private void handleRequestUpdateQuickItems() {
        if (transactionState != TransactionState.SCANNING_IN_PROGRESS) {
            String error = "Cannot update quick items when transaction state is not SCANNING_IN_PROGRESS. Current " +
                    "transaction state: " + transactionState;
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        Set<String> itemUpcsInTransaction =
                transaction.getLineItems().stream().map(LineItem::getItemUpc).collect(Collectors.toSet());
        List<ItemDto> quickItemDtos =
                itemService.getRandomItemsNotIn(itemUpcsInTransaction, ConstVals.QUICK_ITEMS_COUNT).stream()
                        .map(ItemDto::from).toList();

        dispatchPosEvent(new PosEvent(PosEventType.DO_UPDATE_QUICK_ITEMS, Map.of(ConstKeys.ITEM_DTOS, quickItemDtos)));
    }

    private void handleRequestStartPayWithCardProcess() {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Request to start card payment process");
        }

        if (transactionState != TransactionState.SCANNING_IN_PROGRESS) {
            String error = "Cannot start card payment process when transaction state is not SCANNING_IN_PROGRESS";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        if (transaction.getLineItems().isEmpty()) {
            String error = "Cannot start card payment process when transaction has no line items";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        if (transaction.getLineItems().stream().allMatch(LineItem::isVoided)) {
            String error = "Cannot start card payment process when transaction has only voided line items";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        transactionState = TransactionState.AWAITING_CARD_PAYMENT;
        computeDiscountsAndSave();

        dispatchPosEvent(new PosEvent(PosEventType.START_PAY_WITH_CARD_PROCESS));
        dispatchPosEvent(new PosEvent(PosEventType.LOG, Map.of(ConstKeys.MESSAGE, "Card payment process started.")));
    }

    private void handleRequestStartPayWithCashProcesss() {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Request to start cash payment process");
        }

        if (transactionState != TransactionState.SCANNING_IN_PROGRESS) {
            String error = "Cannot start cash payment process when transaction state is not SCANNING_IN_PROGRESS";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        if (transaction.getLineItems().isEmpty()) {
            String error = "Cannot start cash payment process when transaction has no line items";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        if (transaction.getLineItems().stream().allMatch(LineItem::isVoided)) {
            String error = "Cannot start cash payment process when transaction has only voided line items";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        transactionState = TransactionState.AWAITING_CASH_PAYMENT;
        computeDiscountsAndSave();

        dispatchPosEvent(new PosEvent(PosEventType.START_PAY_WITH_CASH_PROCESS));
        dispatchPosEvent(new PosEvent(PosEventType.LOG, Map.of(ConstKeys.MESSAGE, "Cash payment process started.")));
    }

    private void computeDiscountsAndSave() {
        DiscountComputation computation = discountService.computeDiscounts(getTransactionDto());
        if (computation == null) {
            String error = "Failed to compute discounts for transaction";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        transaction.setDiscountAmount(computation.getDiscountAmount());
        transactionService.recomputeAndSaveTransaction(transaction);

        StringBuilder builder = new StringBuilder();
        computation.getAppliedDiscounts().forEach((itemUpc, discount) ->
                builder.append("\n\tItem: ").append(itemUpc).append(", Discount: ").append(discount));
        dispatchPosEvent(new PosEvent(PosEventType.LOG, Map.of(ConstKeys.MESSAGE, "Discounts applied: " + builder)));
    }

    private void handleRequestEnterCardNumber(@NonNull PosEvent event) {
        if (transactionState != TransactionState.AWAITING_CARD_PAYMENT) {
            String error = "Cannot enter card number when transaction state is not AWAITING_CARD_PAYMENT. " +
                    "Current transaction state: " + transactionState;
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        String cardNumber = event.getProperty(ConstKeys.CARD_NUMBER, String.class);
        if (cardNumber == null) {
            String error = "Cannot enter card number because card number is null";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        transaction.setAmountTendered(transaction.getTotal());
        transaction.setChangeDue(BigDecimal.ZERO);

        dispatchPosEvent(new PosEvent(PosEventType.REQUEST_COMPLETE_TRANSACTION));
    }

    private void handleRequestInsertCash(@NonNull PosEvent event) {
        if (transactionState != TransactionState.AWAITING_CASH_PAYMENT) {
            String error = "Cannot insert cash when transaction state is not AWAITING_CASH_PAYMENT. " +
                    "Current transaction state: " + transactionState;
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        String cashAmountToAddString = event.getProperty(ConstKeys.CASH_AMOUNT, String.class);

        if (cashAmountToAddString == null) {
            String error = "Cannot insert cash because cash amount is null";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        if (!UtilMethods.isDouble(cashAmountToAddString)) {
            String error = "Cannot insert cash because cash amount is not a number: " + cashAmountToAddString;
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        BigDecimal cashAmountToAdd = new BigDecimal(cashAmountToAddString);
        transaction.setAmountTendered(transaction.getAmountTendered().add(cashAmountToAdd));

        if (transaction.getAmountTendered().compareTo(transaction.getTotal()) >= 0) {
            transaction.setChangeDue(transaction.getAmountTendered().subtract(transaction.getTotal()));
            dispatchPosEvent(new PosEvent(PosEventType.REQUEST_COMPLETE_TRANSACTION));
        } else {
            BigDecimal amountNeeded = transaction.getTotal().subtract(transaction.getAmountTendered());
            TransactionDto transactionDto = getTransactionDto();
            dispatchPosEvent(new PosEvent(PosEventType.INSUFFICIENT_FUNDS,
                    Map.of(ConstKeys.AMOUNT_NEEDED, amountNeeded, ConstKeys.TRANSACTION_DTO, transactionDto)));
            dispatchPosEvent(new PosEvent(PosEventType.LOG, Map.of(ConstKeys.MESSAGE,
                    "Cash inserted: " + cashAmountToAddString + ". Amount still needed: " + amountNeeded)));
        }
    }

    private void handleRequestCancelPayment() {
        if (!transactionState.isAwaitingPayment()) {
            String error =
                    "Cannot cancel payment when transaction is not awaiting payment. Current transaction state: " +
                            transactionState;
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        transactionState = TransactionState.SCANNING_IN_PROGRESS;
        transaction.setDiscountAmount(BigDecimal.ZERO);
        transactionService.recomputeAndSaveTransaction(transaction);

        dispatchPosEvent(new PosEvent(PosEventType.DO_CANCEL_PAYMENT));
        dispatchPosEvent(new PosEvent(PosEventType.LOG, Map.of(ConstKeys.MESSAGE,
                "Payment cancelled. Any discounts that were applied have been removed.")));
    }

    private void handleRequestCompleteTransaction() {
        if (!transactionState.isAwaitingPayment()) {
            String error = "Cannot complete transaction when transaction is not awaiting payment. Current transaction" +
                    " state: " + transactionState;
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.MESSAGE, error)));
            return;
        }

        completeTransaction();
    }

    /**
     * Set the POS system for this component.
     *
     * @param posSystem The POS system.
     */
    public void setPosSystem(@NonNull PosSystem posSystem) {
        this.posSystem = posSystem;
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Set POS system for POS component: " + posSystem);
        }
    }

    /**
     * Check if a transaction is completed.
     *
     * @return True if the transaction is non-null and completed, false otherwise.
     */
    public boolean isTransactionTendered() {
        return transaction != null && transaction.isTendered();
    }

    /**
     * Check if a transaction is editable. A transaction is editable only if scanning is in progress.
     *
     * @return True if the transaction is non-null and editable, false otherwise.
     */
    public boolean isTransactionEditable() {
        return transactionState == TransactionState.SCANNING_IN_PROGRESS;
    }

    @Override
    public void bootUp() {
        if (posSystem == null) {
            throw new IllegalStateException("POS system must be set to non-null value before calling bootUp()");
        }

        if (Application.DEBUG) {
            System.out.println("[PosComponent] Booting up POS component: " + this);
        }

        on = true;
        transaction = null;
        shuttingDown = false;
        childComponents.forEach(IComponent::bootUp);
        transactionState = TransactionState.NOT_STARTED;

        itemBookLoaderComponent.loadItemBook(itemService);
        dispatchPosEvent(new PosEvent(PosEventType.POS_BOOTUP, Map.of(ConstKeys.POS_SYSTEM_ID, posSystem.getId())));

        if (Application.DEBUG) {
            System.out.println("[PosComponent] POS component booted up: " + this);
        }
    }

    @Override
    public void update() {
        // Do not process events if the POS component is off. A PosComponent that is off should still have its
        // update method called so that it can continue to process events when it is turned back on.
        if (!on) {
            return;
        }

        childComponents.forEach(IComponent::update);
        posEventListeners.forEach(listener -> {
            Set<PosEventType> eventTypesToListenFor = listener.getEventTypesToListenFor();
            eventTypesToListenFor.forEach(type -> {
                List<PosEvent> eventsOfType = events.get(type);
                if (eventsOfType != null) {
                    eventsOfType.forEach(listener::onPosEvent);
                }
            });
        });
        events.clear();

        // Turn off the POS component if it is shutting down so that it and its children do not process any more events
        if (shuttingDown) {
            if (Application.DEBUG) {
                System.out.println("[PosComponent] POS component has been shut down: " + this);
            }
            System.exit(0);
        }
    }

    /**
     * Start a new transaction. Accepts a {@link PosEvent} instance that is nullable. If the event contains a
     * String property with the key {@link ConstKeys#ITEM_UPC}, then a {@link PosEventType#REQUEST_ADD_ITEM}
     * event is triggered. Method is package-private for testing purposes.
     *
     * @param event The event that triggered the transaction. (Nullable)
     */
    void startTransaction(PosEvent event) {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Starting new transaction: " + this);
        }

        transaction = transactionService.createAndPersist(posSystem.getId(), transactionNumber);
        transactionNumber++;
        transactionState = TransactionState.SCANNING_IN_PROGRESS;

        dispatchPosEvent(new PosEvent(PosEventType.TRANSACTION_STARTED,
                Map.of(ConstKeys.TRANSACTION_NUMBER, transaction.getTransactionNumber())));
        dispatchPosEvent(new PosEvent(PosEventType.LOG,
                Map.of(ConstKeys.MESSAGE, "Transaction " + transaction.getTransactionNumber() + " started.")));

        if (Application.DEBUG) {
            System.out.println("[PosComponent] New transaction started: " + this);
        }

        List<ItemDto> quickItemDtos =
                itemService.getRandomItems(ConstVals.QUICK_ITEMS_COUNT).stream().map(ItemDto::from).toList();
        dispatchPosEvent(new PosEvent(PosEventType.DO_UPDATE_QUICK_ITEMS, Map.of(ConstKeys.ITEM_DTOS, quickItemDtos)));

        if (event != null && event.containsProperty(ConstKeys.ITEM_UPC)) {
            dispatchPosEvent(new PosEvent(PosEventType.REQUEST_ADD_ITEM,
                    Map.of(ConstKeys.ITEM_UPC, event.getProperty(ConstKeys.ITEM_UPC))));
        }
    }

    /**
     * Void the current transaction. Package-private for testing purposes.
     */
    void voidTransaction() {
        transactionState = TransactionState.VOIDED;
        transaction.setVoided(true);
        transactionService.saveTransaction(transaction);
        dispatchPosEvent(new PosEvent(PosEventType.TRANSACTION_VOIDED));
        dispatchPosEvent(new PosEvent(PosEventType.LOG,
                Map.of(ConstKeys.MESSAGE, "Transaction " + transaction.getTransactionNumber() + " voided.")));
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Transaction voided: " + this);
        }
    }

    /**
     * Complete the current transaction. Package-private for testing purposes.
     */
    void completeTransaction() {
        transactionState = TransactionState.COMPLETED;

        transaction.setTendered(true);
        transaction.setTimeCompleted(LocalDateTime.now());
        transactionService.saveTransaction(transaction);

        TransactionDto transactionDto = getTransactionDto();
        dispatchPosEvent(
                new PosEvent(PosEventType.TRANSACTION_COMPLETED, Map.of(ConstKeys.TRANSACTION_DTO, transactionDto)));
        dispatchPosEvent(new PosEvent(PosEventType.LOG,
                Map.of(ConstKeys.MESSAGE, "Transaction " + transaction.getTransactionNumber() + " completed.")));

        if (Application.DEBUG) {
            System.out.println("[PosComponent] Transaction completed: " + this);
        }
    }

    /**
     * Resets the pos component to its initial state. Package-private for testing purposes.
     */
    void resetPos() {
        transaction = null;
        transactionState = TransactionState.NOT_STARTED;

        dispatchPosEvent(new PosEvent(PosEventType.POS_RESET));
        dispatchPosEvent(new PosEvent(PosEventType.LOG, Map.of(ConstKeys.MESSAGE, "POS system reset.")));

        if (Application.DEBUG) {
            System.out.println("[PosComponent] POS component reset: " + this);
        }
    }

    private List<LineItemDto> getLineItemDtos() {
        return transaction.getLineItems().stream().map(lineItem -> {
            Item item = itemService.getItemByUpc(lineItem.getItemUpc());
            return LineItemDto.from(lineItem, item);
        }).toList();
    }

    private TransactionDto getTransactionDto() {
        List<LineItemDto> lineItemDtos = getLineItemDtos();
        return TransactionDto.from(lineItemDtos, posSystem.getStoreName(), posSystem.getPosLane(),
                transaction.getTransactionNumber(), transaction.getSubtotal(), transaction.getDiscountAmount(),
                transaction.getTaxes(), transaction.getTotal(), transaction.getAmountTendered(),
                transaction.getChangeDue());
    }

    /**
     * Register a child component with this component. Child components will receive boot up, update, and shutdown
     * calls, but will not receive POS events.
     *
     * @param component The child component to register.
     */
    public void registerChildComponent(@NonNull IComponent component) {
        childComponents.add(component);
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Registered child component: " + component);
        }
    }

    @Override
    public void registerPosEventListener(@NonNull IPosEventListener listener) {
        posEventListeners.add(listener);
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Registered POS event listener: " + listener);
        }
    }

    @Override
    public void unregisterPosEventListener(@NonNull IPosEventListener listener) {
        posEventListeners.remove(listener);
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Unregistered POS event listener: " + listener);
        }
    }
}

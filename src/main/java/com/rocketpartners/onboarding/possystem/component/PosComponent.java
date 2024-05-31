package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.component.journal.IPosJournalListener;
import com.rocketpartners.onboarding.possystem.component.journal.PosJournalComponent;
import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.constant.ConstVals;
import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.display.IController;
import com.rocketpartners.onboarding.possystem.display.dto.ItemDto;
import com.rocketpartners.onboarding.possystem.display.dto.LineItemDto;
import com.rocketpartners.onboarding.possystem.display.dto.TransactionDto;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.IPosEventManager;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import com.rocketpartners.onboarding.possystem.model.Item;
import com.rocketpartners.onboarding.possystem.model.LineItem;
import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.service.ItemService;
import com.rocketpartners.onboarding.possystem.service.TransactionService;
import com.rocketpartners.onboarding.possystem.utils.UtilMethods;
import lombok.*;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for a POS system. This class is responsible for handling POS events and managing transactions.
 */
@ToString
public class PosComponent implements IComponent, IPosEventManager {

    private final ItemBookLoaderComponent itemBookLoaderComponent;

    private final TransactionService transactionService;
    private final ItemService itemService;
    private final PosJournalComponent journalComponent;

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
     * Constructor that accepts a transaction service and an item service. Constructs a new {@link PosJournalComponent}
     * instance. The transaction state is set to NOT_STARTED and the transaction number is set to 1.
     *
     * @param transactionService The transaction service.
     * @param itemService        The item service.
     */
    public PosComponent(@NonNull ItemBookLoaderComponent itemBookLoaderComponent,
                        @NonNull TransactionService transactionService, @NonNull ItemService itemService) {
        this(itemBookLoaderComponent, transactionService, itemService, new PosJournalComponent());
    }

    /**
     * Constructor that accepts a transaction service, an item service, and a POS journal component. The transaction
     * state is set to NOT_STARTED and the transaction number is set to 1.
     *
     * @param transactionService The transaction service.
     * @param itemService        The item service.
     * @param journalComponent   The POS journal component.
     */
    public PosComponent(@NonNull ItemBookLoaderComponent itemBookLoaderComponent,
                        @NonNull TransactionService transactionService, @NonNull ItemService itemService,
                        @NonNull PosJournalComponent journalComponent) {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Creating POS component");
        }
        this.itemBookLoaderComponent = itemBookLoaderComponent;
        this.transactionService = transactionService;
        this.itemService = itemService;
        this.journalComponent = journalComponent;
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
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
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
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            }

            if (!isTransactionEditable()) {
                String error = "Cannot add item to transaction when transaction is not editable. Current " +
                        "transaction state: " + transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            }

            return;
        }

        String itemUpc = event.getProperty(ConstKeys.ITEM_UPC, String.class);

        if (itemUpc == null) {
            String error = "Cannot add item to transaction because item UPC is null";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        if (itemUpc.isBlank()) {
            String error = "Cannot add item to transaction because item UPC is blank";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        if (itemService.itemExists(itemUpc) && transactionService.addItemToTransaction(transaction, itemUpc)) {
            TransactionDto transactionDto = getTransactionDto();

            Item item = itemService.getItemByUpc(itemUpc);
            ItemDto itemDto = ItemDto.from(item);

            dispatchPosEvent(new PosEvent(PosEventType.ITEM_ADDED, Map.of(
                    ConstKeys.ITEM_DTO, itemDto,
                    ConstKeys.TRANSACTION_DTO, transactionDto)));

            if (Application.DEBUG) {
                System.out.println("[PosComponent] Item added to transaction: " + itemUpc);
            }
        } else {
            String error = "Cannot add item to transaction because item with UPC [ " + itemUpc + " ] does not exist";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
        }
    }

    private void handleRequestRemoveItem(@NonNull PosEvent event) {
        if (transactionState != TransactionState.SCANNING_IN_PROGRESS || !isTransactionEditable()) {
            if (transactionState != TransactionState.SCANNING_IN_PROGRESS) {
                String error = "Cannot remove item from transaction when transaction state is not " +
                        "SCANNING_IN_PROGRESS. " +
                        "Current transaction state: " + transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            }

            if (!isTransactionEditable()) {
                String error = "Cannot remove item from transaction when transaction is not editable. Current " +
                        "transaction state: " + transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            }

            return;
        }

        String itemUpc = event.getProperty(ConstKeys.ITEM_UPC, String.class);
        if (itemUpc == null) {
            String error = "Cannot remove item from transaction because item UPC is null";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        if (itemService.itemExists(itemUpc) && transactionService.removeItemFromTransaction(transaction, itemUpc)) {
            TransactionDto transactionDto = getTransactionDto();

            Item item = itemService.getItemByUpc(itemUpc);
            ItemDto itemDto = ItemDto.from(item);

            dispatchPosEvent(new PosEvent(PosEventType.ITEM_REMOVED, Map.of(
                    ConstKeys.ITEM_DTO, itemDto,
                    ConstKeys.TRANSACTION_DTO, transactionDto)));
        } else {
            String error = "Cannot remove item from transaction because item with UPC " + itemUpc + " does not exist";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
        }
    }

    private void handleRequestVoidLineItems(@NonNull PosEvent event) {
        if (transactionState == TransactionState.NOT_STARTED || !isTransactionEditable()) {
            if (transactionState == TransactionState.NOT_STARTED) {
                String error = "Cannot void line items when transaction is not in progress. Current " +
                        "transaction state: " + transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            }

            if (!isTransactionEditable()) {
                String error = "Cannot void line items when transaction is not editable. Current " +
                        "transaction state: " + transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            }

            return;
        }

        Collection<String> itemUpcs = (Collection<String>) event.getProperty(ConstKeys.ITEM_UPCS);
        if (itemUpcs == null) {
            String error = "Cannot void line items because item UPCs is null";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        itemUpcs.forEach(it -> transactionService.voidLineItemInTransaction(transaction, it));

        TransactionDto transactionDto = getTransactionDto();

        dispatchPosEvent(new PosEvent(PosEventType.LINE_ITEMS_VOIDED,
                Map.of(ConstKeys.TRANSACTION_DTO, transactionDto)));
    }

    private void handleRequestVoidTransaction() {
        if (transactionState == TransactionState.NOT_STARTED || !isTransactionEditable()) {
            if (transactionState == TransactionState.NOT_STARTED) {
                String error = "Cannot void transaction when transaction is not in progress. Current " +
                        "transaction state: " + transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            }

            if (!isTransactionEditable()) {
                String error = "Cannot void transaction when transaction is not editable. Current " +
                        "transaction state: " + transactionState;
                System.err.println(error);
                dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
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
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        Set<String> itemUpcsInTransaction =
                transaction.getLineItems().stream().map(LineItem::getItemUpc).collect(Collectors.toSet());
        List<ItemDto> quickItemDtos = itemService.getRandomItemsNotIn(itemUpcsInTransaction,
                ConstVals.QUICK_ITEMS_COUNT).stream().map(ItemDto::from).toList();

        dispatchPosEvent(new PosEvent(PosEventType.DO_UPDATE_QUICK_ITEMS, Map.of(ConstKeys.ITEM_DTOS, quickItemDtos)));
    }

    private void handleRequestStartPayWithCardProcess() {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Request to start card payment process");
        }

        if (transactionState != TransactionState.SCANNING_IN_PROGRESS) {
            String error = "Cannot start card payment process when transaction state is not SCANNING_IN_PROGRESS";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        if (transaction.getLineItems().isEmpty()) {
            String error = "Cannot start card payment process when transaction has no line items";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        if (transaction.getLineItems().stream().allMatch(LineItem::isVoided)) {
            String error = "Cannot start card payment process when transaction has only voided line items";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }


        transactionState = TransactionState.AWAITING_CARD_PAYMENT;
        dispatchPosEvent(new PosEvent(PosEventType.START_PAY_WITH_CARD_PROCESS));
    }

    private void handleRequestStartPayWithCashProcesss() {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Request to start cash payment process");
        }

        if (transactionState != TransactionState.SCANNING_IN_PROGRESS) {
            String error = "Cannot start cash payment process when transaction state is not SCANNING_IN_PROGRESS";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        if (transaction.getLineItems().isEmpty()) {
            String error = "Cannot start cash payment process when transaction has no line items";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        if (transaction.getLineItems().stream().allMatch(LineItem::isVoided)) {
            String error = "Cannot start cash payment process when transaction has only voided line items";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        transactionState = TransactionState.AWAITING_CASH_PAYMENT;
        dispatchPosEvent(new PosEvent(PosEventType.START_PAY_WITH_CASH_PROCESS));
    }

    private void handleRequestEnterCardNumber(@NonNull PosEvent event) {
        if (transactionState != TransactionState.AWAITING_CARD_PAYMENT) {
            String error = "Cannot enter card number when transaction state is not AWAITING_CARD_PAYMENT. " +
                    "Current transaction state: " + transactionState;
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        String cardNumber = event.getProperty(ConstKeys.CARD_NUMBER, String.class);
        if (cardNumber == null) {
            String error = "Cannot enter card number because card number is null";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
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
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        String cashAmountToAddString = event.getProperty(ConstKeys.CASH_AMOUNT, String.class);

        if (cashAmountToAddString == null) {
            String error = "Cannot insert cash because cash amount is null";
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        if (!UtilMethods.isDouble(cashAmountToAddString)) {
            String error = "Cannot insert cash because cash amount is not a number: " + cashAmountToAddString;
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
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
        }
    }

    private void handleRequestCancelPayment() {
        if (!transactionState.isAwaitingPayment()) {
            String error = "Cannot cancel payment when transaction is not awaiting payment. Current transaction " +
                    "state: " + transactionState;
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        transactionState = TransactionState.SCANNING_IN_PROGRESS;
        dispatchPosEvent(new PosEvent(PosEventType.DO_CANCEL_PAYMENT));
    }

    private void handleRequestCompleteTransaction() {
        if (!transactionState.isAwaitingPayment()) {
            String error = "Cannot complete transaction when transaction is not awaiting payment. Current transaction" +
                    " state: " + transactionState;
            System.err.println(error);
            dispatchPosEvent(new PosEvent(PosEventType.ERROR, Map.of(ConstKeys.ERROR, error)));
            return;
        }

        completeTransaction();
    }

    /**
     * Add a journal listener to this component.
     *
     * @param listener The journal listener.
     */
    public void addJournalListener(@NonNull IPosJournalListener listener) {
        journalComponent.addJournalListener(listener);
    }

    /**
     * Remove a journal listener from this component.
     *
     * @param listener The journal listener.
     */
    public void removeJournalListener(@NonNull IPosJournalListener listener) {
        journalComponent.removeJournalListener(listener);
    }

    /**
     * Log a message to the journal.
     *
     * @param message The message to log.
     */
    public void logToJournal(@NonNull String message) {
        journalComponent.log(message);
    }

    /**
     * Log an error to the journal.
     *
     * @param message The error message.
     */
    public void errorToJournal(@NonNull String message) {
        journalComponent.error(message);
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
     * Check if a transaction is voided.
     *
     * @return True if the transaction is non-null and voided, false otherwise.
     */
    public boolean isTransactionVoided() {
        return transaction != null && transaction.isVoided();
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

        SwingUtilities.invokeLater(() -> {
            itemBookLoaderComponent.loadItemBook(itemService);
            dispatchPosEvent(new PosEvent(PosEventType.POS_BOOTUP, Map.of(ConstKeys.POS_SYSTEM_ID, posSystem.getId())));
        });

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
     */
    void startTransaction(PosEvent event) {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Starting new transaction: " + this);
        }

        transaction = transactionService.createAndPersist(posSystem.getId(), transactionNumber);
        transactionNumber++;
        transactionState = TransactionState.SCANNING_IN_PROGRESS;

        dispatchPosEvent(new PosEvent(PosEventType.TRANSACTION_STARTED, Map.of(
                ConstKeys.TRANSACTION_NUMBER, transaction.getTransactionNumber())));

        if (Application.DEBUG) {
            System.out.println("[PosComponent] New transaction started: " + this);
        }

        List<ItemDto> quickItemDtos =
                itemService.getRandomItems(ConstVals.QUICK_ITEMS_COUNT).stream().map(ItemDto::from).toList();
        dispatchPosEvent(new PosEvent(PosEventType.DO_UPDATE_QUICK_ITEMS, Map.of(ConstKeys.ITEM_DTOS, quickItemDtos)));

        if (event != null && event.containsProperty(ConstKeys.ITEM_UPC)) {
            dispatchPosEvent(new PosEvent(PosEventType.REQUEST_ADD_ITEM, Map.of(ConstKeys.ITEM_UPC,
                    event.getProperty(ConstKeys.ITEM_UPC))));
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
        dispatchPosEvent(new PosEvent(PosEventType.TRANSACTION_COMPLETED,
                Map.of(ConstKeys.TRANSACTION_DTO, transactionDto)));

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
        return TransactionDto.from(
                lineItemDtos,
                posSystem.getStoreName(),
                posSystem.getPosLane(),
                transaction.getTransactionNumber(),
                transaction.getSubtotal(),
                transaction.getDiscounts(),
                transaction.getTaxes(),
                transaction.getTotal(),
                transaction.getAmountTendered(),
                transaction.getChangeDue()
        );
    }

    /**
     * Register a child controller with this controller and also add it as an event listener. Child controllers will
     * receive boot up, update, and shutdown calls, and POS events.
     *
     * @param controller The child controller to register.
     */
    public void registerChildController(@NonNull IController controller) {
        childComponents.add(controller);
        posEventListeners.add(controller);
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Registered child controller: " + controller);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the listener is an {@link IController} instance, then you should use the {@link #registerChildController}
     * method instead.
     *
     * @param listener The listener to register.
     */
    @Override
    public void registerPosEventListener(@NonNull IPosEventListener listener) {
        posEventListeners.add(listener);
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Registered POS event listener: " + listener);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the listener is an {@link IController} instance, then you should use the {@link #registerChildController}
     * method instead.
     *
     * @param listener The listener to unregister.
     */
    @Override
    public void unregisterPosEventListener(@NonNull IPosEventListener listener) {
        posEventListeners.remove(listener);
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Unregistered POS event listener: " + listener);
        }
    }
}

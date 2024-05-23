package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.component.journal.IPosJournalListener;
import com.rocketpartners.onboarding.possystem.component.journal.PosJournalComponent;
import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.display.IController;
import com.rocketpartners.onboarding.possystem.display.dto.LineItemDto;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.IPosEventManager;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import com.rocketpartners.onboarding.possystem.model.Item;
import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.service.ItemService;
import com.rocketpartners.onboarding.possystem.service.TransactionService;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

/**
 * Controller for a POS system. This class is responsible for handling POS events and managing transactions.
 */
@ToString
public class PosComponent implements IComponent, IPosEventManager {

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
    private Transaction transaction;
    @Getter
    @Setter
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
    public PosComponent(@NonNull TransactionService transactionService,
                        @NonNull ItemService itemService) {
        this(transactionService, itemService, new PosJournalComponent());
    }

    /**
     * Constructor that accepts a transaction service, an item service, and a POS journal component. The transaction
     * state is set to NOT_STARTED and the transaction number is set to 1.
     *
     * @param transactionService The transaction service.
     * @param itemService        The item service.
     * @param journalComponent   The POS journal component.
     */
    public PosComponent(@NonNull TransactionService transactionService,
                        @NonNull ItemService itemService,
                        @NonNull PosJournalComponent journalComponent) {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Creating POS component");
        }
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
        transactionState = TransactionState.NOT_STARTED;
        childComponents.forEach(IComponent::bootUp);
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
            on = false;
            shuttingDown = false;
            if (Application.DEBUG) {
                System.out.println("[PosComponent] POS component has been shut down: " + this);
            }
        }
    }

    @Override
    public void shutdown() {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Shutting down POS component: " + this);
        }
        transaction = null;
        transactionState = TransactionState.NOT_STARTED;
        childComponents.forEach(IComponent::shutdown);
        dispatchPosEvent(new PosEvent(PosEventType.POS_SHUTDOWN));
        // The POS component will be fully shutdown after the next update
        shuttingDown = true;
    }

    /**
     * Start a new transaction. Package-private for testing purposes.
     */
    void startTransaction() {
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Starting new transaction: " + this);
        }
        transaction = transactionService.createAndPersist(posSystem.getId(), transactionNumber);
        transactionNumber++;
        transactionState = TransactionState.SCANNING_IN_PROGRESS;
        dispatchPosEvent(new PosEvent(PosEventType.TRANSACTION_STARTED));
        if (Application.DEBUG) {
            System.out.println("[PosComponent] New transaction started: " + this);
        }
    }

    /**
     * Void the current transaction. Package-private for testing purposes.
     */
    void voidTransaction() {
        transactionState = TransactionState.VOIDED;
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
        dispatchPosEvent(new PosEvent(PosEventType.TRANSACTION_COMPLETED));
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
     * Unregister a child controller from this controller and also remove it as an event listener. Child controllers
     * will no longer receive boot up, update, or shutdown calls, or POS events.
     *
     * @param controller The child controller to unregister.
     */
    public void unregisterChildController(@NonNull IController controller) {
        childComponents.remove(controller);
        posEventListeners.remove(controller);
        if (Application.DEBUG) {
            System.out.println("[PosComponent] Unregistered child controller: " + controller);
        }
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

    /**
     * {@inheritDoc}
     * <p>
     * If the listener is an {@link IController} instance, then you should use the {@link #registerChildController} and
     * {@link #unregisterChildController} methods instead.
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
     * If the listener is an {@link IController} instance, then you should use the {@link #registerChildController} and
     * {@link #unregisterChildController} methods instead.
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

    private void handlePosEvent(@NonNull PosEvent event) {
        switch (event.getType()) {
            case REQUEST_START_TRANSACTION -> {
                if (transactionState != TransactionState.NOT_STARTED) {
                    System.err.println("[PosComponent] Request to start transaction not allowed when transaction " +
                            "state is not NOT_STARTED");
                    return;
                }
                startTransaction();
            }

            case REQUEST_ADD_ITEM -> {
                if (transactionState != TransactionState.SCANNING_IN_PROGRESS) {
                    System.err.println("[PosComponent] Request to add item not allowed when transaction state is not " +
                            "SCANNING_IN_PROGRESS");
                    return;
                }
                String itemUpc = event.getProperty(ConstKeys.ITEM_UPC, String.class);
                if (itemUpc == null) {
                    System.err.println("[PosComponent] Request to add item failed because item UPC is null");
                    return;
                }
                if (itemService.itemExists(itemUpc) && transactionService.addItemToTransaction(transaction, itemUpc)) {
                    List<LineItemDto> lineItemDtos = transaction.getLineItems().stream()
                            .map(lineItem -> {
                                Item item = itemService.getItemByUpc(lineItem.getItemUpc());
                                return LineItemDto.from(lineItem, item);
                            }).toList();
                    dispatchPosEvent(new PosEvent(PosEventType.ITEM_ADDED, Map.of(ConstKeys.LINE_ITEM_DTOS,
                            lineItemDtos)));
                } else {
                    System.err.println("[PosComponent] Request to add item failed because item with UPC " + itemUpc +
                            " does not exist");
                }
            }

            case REQUEST_REMOVE_ITEM -> {
                if (transactionState != TransactionState.SCANNING_IN_PROGRESS) {
                    System.err.println("[PosComponent] Request to remove item not allowed when transaction state is " +
                            "not " +
                            "SCANNING_IN_PROGRESS");
                    return;
                }
                String itemUpc = event.getProperty(ConstKeys.ITEM_UPC, String.class);
                if (itemUpc == null) {
                    System.err.println("[PosComponent] Request to add item failed because item UPC is null");
                    return;
                }
                if (itemService.itemExists(itemUpc) && transactionService.removeItemFromTransaction(transaction,
                        itemUpc)) {
                    List<LineItemDto> lineItemDtos = transaction.getLineItems().stream()
                            .map(lineItem -> {
                                Item item = itemService.getItemByUpc(lineItem.getItemUpc());
                                return LineItemDto.from(lineItem, item);
                            }).toList();
                    dispatchPosEvent(new PosEvent(PosEventType.ITEM_REMOVED, Map.of(ConstKeys.LINE_ITEM_DTOS,
                            lineItemDtos)));
                } else {
                    System.err.println("[PosComponent] Request to add item failed because item with UPC " + itemUpc +
                            " does not exist");
                }
            }

            case REQUEST_VOID_LINE_ITEMS -> {
                if (transactionState == TransactionState.NOT_STARTED) {
                    System.err.println("[PosComponent] Request to void line items not allowed when transaction state" +
                            " is NOT_STARTED");
                    return;
                }
                Collection<String> itemUpcs = (Collection<String>) event.getProperty(ConstKeys.ITEM_UPCS);
                if (itemUpcs == null) {
                    System.err.println("[PosComponent] Request to void line items failed because item UPCs is null");
                    return;
                }
                itemUpcs.forEach(it -> transactionService.voidLineItemInTransaction(transaction, it));
                List<LineItemDto> lineItemDtos = transaction.getLineItems().stream()
                        .map(lineItem -> {
                            Item item = itemService.getItemByUpc(lineItem.getItemUpc());
                            return LineItemDto.from(lineItem, item);
                        }).toList();
                dispatchPosEvent(new PosEvent(PosEventType.LINE_ITEMS_VOIDED, Map.of(ConstKeys.LINE_ITEM_DTOS,
                        lineItemDtos)));
            }

            case REQUEST_VOID_TRANSACTION -> {
                if (transactionState == TransactionState.NOT_STARTED) {
                    System.err.println("[PosComponent] Request to void transaction not allowed when transaction state" +
                            " is NOT_STARTED");
                    return;
                }
                voidTransaction();
            }

            case REQUEST_COMPLETE_TRANSACTION -> {
                if (!transactionState.isAwaitingPayment()) {
                    System.err.println("[PosComponent] Request to complete transaction not allowed when transaction " +
                            "state is not AWAITING_CARD_PAYMENT or AWAITING_CASH_PAYMENT");
                    return;
                }
                completeTransaction();
            }

            case REQUEST_RESET_POS -> {
                if (!transactionState.isEnded()) {
                    System.err.println("[PosComponent] Request to reset POS not allowed when transaction state is not" +
                            " VOIDED or COMPLETED");
                    return;
                }
                resetPos();
            }
        }
    }
}

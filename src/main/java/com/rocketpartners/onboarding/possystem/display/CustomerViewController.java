package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.display.dto.ItemDto;
import com.rocketpartners.onboarding.possystem.display.dto.LineItemDto;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Controller for the customer view. This class is responsible for updating the customer view based on POS events.
 */
public class CustomerViewController implements IController {

    private static final Set<PosEventType> eventTypesToListenFor = EnumSet.of(
            PosEventType.POS_BOOTUP,
            PosEventType.POS_RESET,
            PosEventType.TRANSACTION_STARTED,
            PosEventType.TRANSACTION_VOIDED,
            PosEventType.TRANSACTION_COMPLETED,
            PosEventType.DO_UPDATE_QUICK_ITEMS,
            PosEventType.ITEM_ADDED,
            PosEventType.ITEM_REMOVED,
            PosEventType.LINE_ITEMS_VOIDED,
            PosEventType.START_PAY_WITH_CARD_PROCESS,
            PosEventType.DO_CANCEL_PAYMENT
    );

    @NonNull
    private final IPosEventDispatcher parentPosEventDispatcher;
    @NonNull
    private final CustomerView customerView;

    /**
     * Constructor that accepts a parent POS event dispatcher. The transaction state is set to NOT_STARTED and will be
     * updated when the POS system sends an event contained in {@link #getEventTypesToListenFor()}. The customer view
     * is created with the store name and POS lane number.
     *
     * @param parentPosEventDispatcher The parent POS event dispatcher.
     * @param storeName                The store name.
     * @param posLane                  The POS lane number.
     */
    public CustomerViewController(@NonNull IPosEventDispatcher parentPosEventDispatcher,
                                  @NonNull String storeName, int posLane) {
        this.parentPosEventDispatcher = parentPosEventDispatcher;
        customerView = new CustomerView(this, storeName, posLane);
    }

    /**
     * Constructor that accepts a parent POS event dispatcher. The transaction state is set to NOT_STARTED and will be
     * updated when the POS system sends an event contained in {@link #getEventTypesToListenFor()}. Package-private for
     * testing.
     *
     * @param parentPosEventDispatcher The parent POS event dispatcher.
     * @param customerView             The customer view.
     */
    CustomerViewController(@NonNull IPosEventDispatcher parentPosEventDispatcher,
                           @NonNull CustomerView customerView) {
        this.parentPosEventDispatcher = parentPosEventDispatcher;
        this.customerView = customerView;
    }

    @Override
    public Set<PosEventType> getEventTypesToListenFor() {
        return eventTypesToListenFor;
    }

    @Override
    public void dispatchPosEvent(@NonNull PosEvent event) {
        parentPosEventDispatcher.dispatchPosEvent(event);
    }

    @Override
    public void onPosEvent(@NonNull PosEvent posEvent) {
        switch (posEvent.getType()) {
            case POS_BOOTUP, POS_RESET -> setTransactionState(TransactionState.NOT_STARTED);
            case TRANSACTION_STARTED, DO_CANCEL_PAYMENT -> setTransactionState(TransactionState.SCANNING_IN_PROGRESS);
            case DO_UPDATE_QUICK_ITEMS -> {
                List<ItemDto> itemDtos = (List<ItemDto>) posEvent.getProperty(ConstKeys.ITEM_DTOS);
                customerView.updateQuickItems(itemDtos);
            }
            case ITEM_ADDED, ITEM_REMOVED, LINE_ITEMS_VOIDED -> {
                List<LineItemDto> lineitemDtos = (List<LineItemDto>) posEvent.getProperty(ConstKeys.LINE_ITEM_DTOS);
                customerView.updateTransactionsTable(lineitemDtos);
            }
            case START_PAY_WITH_CARD_PROCESS -> setTransactionState(TransactionState.AWAITING_CARD_PAYMENT);
            case TRANSACTION_VOIDED -> setTransactionState(TransactionState.VOIDED);
            case TRANSACTION_COMPLETED -> setTransactionState(TransactionState.COMPLETED);
        }
    }

    /**
     * Sets the transaction state and updates the view according to the state. Package-private for testing.
     *
     * @param transactionState The transaction state.
     */
    void setTransactionState(@NonNull TransactionState transactionState) {
        if (Application.DEBUG) {
            System.out.println("[CustomerViewController] Setting transaction state to: " + transactionState);
        }
        updateView(transactionState);
    }

    private void updateView(@NonNull TransactionState transactionState) {
        switch (transactionState) {
            case NOT_STARTED -> {
                customerView.reset();
                customerView.showTransactionNotStarted();
            }
            case SCANNING_IN_PROGRESS -> customerView.showScanningInProgress();
            case AWAITING_CARD_PAYMENT, AWAITING_CASH_PAYMENT -> customerView.showAwaitingPayment();
            case VOIDED -> customerView.showTransactionVoided();
            case COMPLETED -> customerView.showTransactionCompleted();
        }
    }

    @Override
    public void bootUp() {
        if (Application.DEBUG) {
            System.out.println("[CustomerViewController] Booting up customer view controller");
        }
        customerView.setVisible(true);
    }

    @Override
    public void shutdown() {
        if (Application.DEBUG) {
            System.out.println("[CustomerViewController] Shutting down customer view controller");
        }
        customerView.setVisible(false);
    }
}


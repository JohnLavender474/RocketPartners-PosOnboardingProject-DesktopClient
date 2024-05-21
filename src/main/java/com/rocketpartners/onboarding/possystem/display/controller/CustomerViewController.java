package com.rocketpartners.onboarding.possystem.display.controller;

import com.rocketpartners.onboarding.possystem.component.IComponent;
import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.display.view.CustomerView;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Set;

/**
 * Controller for the customer view. This class is responsible for updating the customer view based on POS events.
 */
@AllArgsConstructor
public class CustomerViewController implements IPosEventListener, IComponent {

    private static final Set<PosEventType> eventTypesToListenFor = Set.of(
            PosEventType.POS_BOOTUP,
            PosEventType.POS_RESET,
            PosEventType.TRANSACTION_STARTED,
            PosEventType.TRANSACTION_VOIDED,
            PosEventType.TRANSACTION_COMPLETED
    );

    @NonNull
    private final CustomerView customerView;
    @NonNull
    private TransactionState transactionState;

    /**
     * Constructor that accepts a customer view. The transaction state is set to NOT_STARTED and will be updated when
     * the POS system sends an event contained in {@link #getEventTypesToListenFor()}.
     *
     * @param customerView The customer view.
     */
    public CustomerViewController(@NonNull CustomerView customerView) {
        this(customerView, TransactionState.NOT_STARTED);
    }

    @Override
    public Set<PosEventType> getEventTypesToListenFor() {
        return eventTypesToListenFor;
    }

    @Override
    public void onPosEvent(PosEvent posEvent) {
        switch (posEvent.getType()) {
            case POS_BOOTUP, POS_RESET -> setTransactionState(TransactionState.NOT_STARTED);
            case TRANSACTION_STARTED -> setTransactionState(TransactionState.SCANNING_IN_PROGRESS);
            case TRANSACTION_VOIDED -> setTransactionState(TransactionState.VOIDED);
            case TRANSACTION_COMPLETED -> setTransactionState(TransactionState.COMPLETED);
        }
    }

    /**
     * Sets the transaction state and updates the view according to the state.
     *
     * @param transactionState The transaction state.
     */
    public void setTransactionState(@NonNull TransactionState transactionState) {
        this.transactionState = transactionState;
        updateView();
    }

    private void updateView() {
        switch (transactionState) {
            case NOT_STARTED -> customerView.showTransactionNotStarted();
            case SCANNING_IN_PROGRESS -> customerView.showScanningInProgress();
            case AWAITING_PAYMENT -> customerView.showAwaitingPayment();
            case VOIDED -> customerView.showTransactionVoided();
            case COMPLETED -> customerView.showTransactionCompleted();
        }
    }

    @Override
    public void bootUp() {
        customerView.setVisible(true);
    }
}


package com.rocketpartners.onboarding.possystem.display.controller;

import com.rocketpartners.onboarding.possystem.component.IComponent;
import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.display.view.CustomerView;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import java.util.Set;

/**
 * Controller for the customer view.
 */
public class CustomerViewController implements IPosEventListener, IComponent {

    private static final Set<PosEventType> eventTypesToListenFor = Set.of();

    private final CustomerView customerView;

    @NonNull
    private TransactionState transactionState;

    /**
     * Constructor for the CustomerViewController.
     *
     * @param customerView The customer view.
     */
    public CustomerViewController(@NonNull CustomerView customerView) {
        this.customerView = customerView;
        transactionState = TransactionState.NOT_STARTED;
    }

    @Override
    public Set<PosEventType> getEventTypesToListenFor() {
        return eventTypesToListenFor;
    }

    @Override
    public void onPosEvent(PosEvent posEvent) {

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


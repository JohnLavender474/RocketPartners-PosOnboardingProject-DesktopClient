package com.rocketpartners.onboarding.possystem.display.controller;

import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.component.IComponent;
import com.rocketpartners.onboarding.possystem.display.view.CustomerView;
import lombok.NonNull;

public class CustomerViewController implements IComponent {

    private final CustomerView view;

    private TransactionState transactionState;

    public CustomerViewController(CustomerView view) {
        this.view = view;
        transactionState = TransactionState.NOT_STARTED;
    }

    public void setTransactionState(@NonNull TransactionState transactionState) {
        this.transactionState = transactionState;
        updateView();
    }

    private void updateView() {
        switch (transactionState) {
            case NOT_STARTED -> {
            }
            case SCANNING_IN_PROGRESS -> {
            }
            case AWAITING_PAYMENT -> {
            }
            case VOIDED -> {
            }
            case COMPLETED -> {
            }
        }
    }

    @Override
    public void bootUp() {
        view.setVisible(true);
    }

    @Override
    public void shutdown() {
        view.setVisible(false);
    }
}


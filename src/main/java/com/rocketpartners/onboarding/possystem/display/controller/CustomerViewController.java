package com.rocketpartners.onboarding.possystem.display.controller;

import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.component.IComponent;
import com.rocketpartners.onboarding.possystem.display.view.CustomerView;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerViewController implements IComponent {

    private final CustomerView customerView;

    @NonNull
    private TransactionState transactionState;

    @Autowired
    public CustomerViewController(@NonNull CustomerView customerView) {
        this.customerView = customerView;
        transactionState = TransactionState.NOT_STARTED;
    }

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

    @Override
    public void shutdown() {
        customerView.setVisible(false);
    }
}


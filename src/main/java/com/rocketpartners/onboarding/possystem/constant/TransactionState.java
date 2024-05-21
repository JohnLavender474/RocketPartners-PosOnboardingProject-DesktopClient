package com.rocketpartners.onboarding.possystem.constant;

/**
 * Enum representing the state of a transaction.
 */
public enum TransactionState {
    NOT_STARTED,
    SCANNING_IN_PROGRESS,
    AWAITING_CARD_PAYMENT,
    AWAITING_CASH_PAYMENT,
    VOIDED,
    COMPLETED;

    /**
     * Returns true if the transaction is VOIDED or COMPLETED.
     *
     * @return True if the transaction has ended.
     */
    public boolean isEnded() {
        return this == VOIDED || this == COMPLETED;
    }

    /**
     * Returns true if the transaction is AWAITING_CARD_PAYMENT or AWAITING_CASH_PAYMENT.
     *
     * @return True if the transaction is awaiting payment.
     */
    public boolean isAwaitingPayment() {
        return this == AWAITING_CARD_PAYMENT || this == AWAITING_CASH_PAYMENT;
    }
}

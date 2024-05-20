package com.rocketpartners.onboarding.possystem.event;

/**
 * Enum for POS event types
 */
public enum PosEventType {
    POS_BOOTUP,
    POS_SHUTDOWN,
    REQUEST_START_TRANSACTION,
    TRANSACTION_STARTED,
    REQUEST_VOID_TRANSACTION,
    TRANSACTION_VOIDED,
    REQUEST_COMPLETE_TRANSACTION,
    TRANSACTION_COMPLETED,
    REQUEST_RESET_POS,
    POS_RESET
}

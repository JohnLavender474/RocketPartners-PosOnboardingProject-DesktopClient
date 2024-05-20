package com.rocketpartners.onboarding.possystem.event;

/**
 * Enum for POS event types
 */
public enum PosEventType {
    POS_BOOTUP,
    POS_SHUTDOWN,
    REQUEST_START_TRANSACTION,
    START_TRANSACTION,
    REQUEST_VOID_TRANSACTION,
    VOID_TRANSACTION,
    REQUEST_COMPLETE_TRANSACTION,
    COMPLETE_TRANSACTION,
}

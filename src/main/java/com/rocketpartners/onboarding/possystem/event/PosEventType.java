package com.rocketpartners.onboarding.possystem.event;

/**
 * Enum for POS event types.
 */
public enum PosEventType {
    LOG,
    ERROR,
    POS_BOOTUP,
    POS_SHUTDOWN,
    REQUEST_SHUTDOWN,
    REQUEST_START_TRANSACTION,
    TRANSACTION_STARTED,
    REQUEST_VOID_TRANSACTION,
    TRANSACTION_VOIDED,
    REQUEST_COMPLETE_TRANSACTION,
    TRANSACTION_COMPLETED,
    REQUEST_OPEN_SCANNER,
    DO_OPEN_SCANNER,
    REQUEST_RESET_POS,
    POS_RESET,
    REQUEST_UPDATE_QUICK_ITEMS,
    DO_UPDATE_QUICK_ITEMS,
    REQUEST_ADD_ITEM,
    ITEM_ADDED,
    REQUEST_REMOVE_ITEM,
    ITEM_REMOVED,
    REQUEST_VOID_LINE_ITEMS,
    LINE_ITEMS_VOIDED,
    REQUEST_PAY_WITH_CASH,
    REQUEST_INSERT_CASH,
    CASH_INSERTED,
    REQUEST_START_PAY_WITH_CARD_PROCESS,
    REQUEST_START_PAY_WITH_CASH_PROCESS,
    START_PAY_WITH_CARD_PROCESS,
    REQUEST_PAY_WITH_CARD,
    REQUEST_ENTER_CARD_NUMBER,
    REQUEST_ENTER_CARD_PIN,
    REQUEST_CANCEL_PAYMENT,
    DO_CANCEL_PAYMENT,
}

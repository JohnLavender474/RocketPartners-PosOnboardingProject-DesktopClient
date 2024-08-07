package com.rocketpartners.onboarding.possystem.event;

/**
 * Enum for POS event types.
 */
public enum PosEventType {
    LOG,
    ERROR,

    POS_BOOTUP,
    REQUEST_RESET_POS,
    POS_RESET,

    REQUEST_START_TRANSACTION,
    TRANSACTION_STARTED,
    REQUEST_VOID_TRANSACTION,
    TRANSACTION_VOIDED,
    REQUEST_COMPLETE_TRANSACTION,
    TRANSACTION_COMPLETED,

    REQUEST_OPEN_SCANNER,
    DO_OPEN_SCANNER,
    REQUEST_OPEN_POLE_DISPLAY,
    DO_OPEN_POLE_DISPLAY,
    REQUEST_SHOW_DISCOUNTS,
    DO_SHOW_DISCOUNTS,

    REQUEST_UPDATE_QUICK_ITEMS,
    DO_UPDATE_QUICK_ITEMS,

    REQUEST_ADD_ITEM,
    ITEM_ADDED,
    REQUEST_REMOVE_ITEM,
    ITEM_REMOVED,
    REQUEST_VOID_LINE_ITEMS,
    LINE_ITEMS_VOIDED,

    REQUEST_START_PAY_WITH_CASH_PROCESS,
    START_PAY_WITH_CASH_PROCESS,
    REQUEST_INSERT_CASH,
    INSUFFICIENT_FUNDS,

    REQUEST_START_PAY_WITH_CARD_PROCESS,
    START_PAY_WITH_CARD_PROCESS,
    REQUEST_PAY_WITH_CARD,
    REQUEST_ENTER_CARD_NUMBER,

    REQUEST_CANCEL_PAYMENT,
    DO_CANCEL_PAYMENT,
}

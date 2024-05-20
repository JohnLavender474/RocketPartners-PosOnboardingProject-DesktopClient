package com.rocketpartners.onboarding.possystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Represents a line item in a transaction. A line item is a multiple of the same item. Transactions are made up of
 * one or more line items.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LineItem {

    private String itemUpc;
    private String transactionId;
    private int quantity;
    private BigDecimal totalPrice;
    private boolean voided;
    private boolean purchased;
}

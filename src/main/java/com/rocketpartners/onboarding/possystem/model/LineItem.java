package com.rocketpartners.onboarding.possystem.model;

import lombok.*;

/**
 * Represents a line item in a transaction. A line item is a multiple of the same item. Transactions are made up of
 * one or more line items.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LineItem {

    @NonNull
    private String itemUpc;
    @NonNull
    private String transactionId;
    private int quantity;
    private boolean voided;
    private boolean purchased;
}

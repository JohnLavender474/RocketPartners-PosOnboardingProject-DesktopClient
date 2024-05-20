package com.rocketpartners.onboarding.possystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a transaction in a point of sale system. A transaction has a unique ID, customer ID, point of sale
 * system ID, transaction number, time completed, line items, subtotal, discounts applied, taxes, total, voided status,
 * and tendered status. Transactions are made up of one or more line items.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    private String id;
    private String customerId;
    private String posSystemId;
    private int transactionNumber;
    private LocalDateTime timeCompleted;
    private List<LineItem> lineItems;
    private BigDecimal subtotal;
    private List<Discount> discountsApplied;
    private BigDecimal taxes;
    private BigDecimal total;
    private boolean voided;
    private boolean tendered;
}

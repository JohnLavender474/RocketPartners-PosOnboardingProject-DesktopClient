package com.rocketpartners.onboarding.possystem.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a transaction in a point of sale system. A transaction has a unique ID, customer ID, point of sale
 * system ID, transaction number, time completed, line items, subtotal, discounts applied, taxes, total, voided status,
 * and tendered status. Transactions are made up of one or more line items.
 */
@Getter
@Setter
@AllArgsConstructor
public class Transaction {

    @NonNull
    private String id;
    @NonNull
    private String posSystemId;
    @NonNull
    private List<LineItem> lineItems;
    @NonNull
    private List<Discount> discountsApplied;
    private int transactionNumber;
    private LocalDateTime timeCreated;
    private LocalDateTime timeCompleted;
    private BigDecimal subtotal;
    private BigDecimal taxes;
    private BigDecimal total;
    private BigDecimal amountTendered;
    private BigDecimal changeDue;
    private String customerId;
    private boolean voided;
    private boolean tendered;

    public Transaction() {
        lineItems = new ArrayList<>();
        discountsApplied = new ArrayList<>();
    }
}

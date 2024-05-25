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
@ToString
@AllArgsConstructor
public class Transaction {

    @NonNull
    private String id;
    @NonNull
    private String posSystemId;
    @NonNull
    private List<LineItem> lineItems;
    private int transactionNumber;
    @NonNull
    private BigDecimal subtotal;
    @NonNull
    private BigDecimal taxes;
    @NonNull
    private BigDecimal discounts;
    @NonNull
    private BigDecimal total;
    @NonNull
    private BigDecimal amountTendered;
    @NonNull
    private BigDecimal changeDue;
    private String customerId;
    private boolean voided;
    private boolean tendered;
    private LocalDateTime timeCreated;
    private LocalDateTime timeCompleted;

    public Transaction() {
        lineItems = new ArrayList<>();
        subtotal = BigDecimal.ZERO;
        taxes = BigDecimal.ZERO;
        discounts = BigDecimal.ZERO;
        total = BigDecimal.ZERO;
        amountTendered = BigDecimal.ZERO;
        changeDue = BigDecimal.ZERO;
    }
}

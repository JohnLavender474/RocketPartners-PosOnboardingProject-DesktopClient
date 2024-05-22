package com.rocketpartners.onboarding.possystem.model;

import lombok.*;

import java.math.BigDecimal;

/**
 * Represents an item that can be sold in a store. Items have a UPC, name, category, description, and unit price. Items
 * are used to create line items in a transaction.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @NonNull
    private String upc;
    @NonNull
    private String name;
    @NonNull
    private BigDecimal unitPrice;
    private String category;
    private String description;
}

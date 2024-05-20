package com.rocketpartners.onboarding.possystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String upc;
    private String name;
    private String category;
    private String description;
    private BigDecimal unitPrice;
}

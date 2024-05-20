package com.rocketpartners.onboarding.possystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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

}

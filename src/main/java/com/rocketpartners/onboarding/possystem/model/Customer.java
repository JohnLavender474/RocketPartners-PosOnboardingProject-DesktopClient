package com.rocketpartners.onboarding.possystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a customer who can make purchases in a store. Customers have a unique ID, name, email, phone number, and
 * address. TODO: Customers can earn loyalty points when they make purchases.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    // TODO: map loyalty points either here or in a separate class/table

}

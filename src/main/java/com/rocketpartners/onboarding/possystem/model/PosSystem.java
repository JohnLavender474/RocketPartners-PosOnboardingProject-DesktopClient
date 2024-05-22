package com.rocketpartners.onboarding.possystem.model;

import lombok.*;

/**
 * Represents a point of sale system in a store. A point of sale system has a unique ID, store name, and a lane number.
 * The lane number is used to identify the point of sale system in the store. The store name is used to identify the
 * store where the point of sale system is located.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PosSystem {

    private String id;
    private String storeName;
    private int posLane;
}

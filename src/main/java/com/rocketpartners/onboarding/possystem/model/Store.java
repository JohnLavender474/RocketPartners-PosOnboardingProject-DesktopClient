package com.rocketpartners.onboarding.possystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a store where a point of sale system is located. A store has a name and an address. The name is used to
 * identify the store in the point of sale system. The address is used to locate the store in the real world.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Store {

    private String name;
    private String address;

}

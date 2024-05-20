package com.rocketpartners.onboarding.possystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PosSystem {

    private String id;
    private String storeName;
    private int posLane;
}

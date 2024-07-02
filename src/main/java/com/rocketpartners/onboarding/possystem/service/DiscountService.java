package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.DiscountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Service class for Discounts. This class provides methods for computing discounts to apply to a transaction and
 * for computing the total discount amount for a list of discounts.
 */
@ToString
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;

    /**
     * Compute the total discount amount for a transaction.
     *
     * @param transaction The transaction for which to compute discounts.
     * @return the total discount amount for the transaction
     */
    public BigDecimal computeDiscountAmountToApplyTo(@NonNull Transaction transaction) {
        // TODO: Implement this method
        return BigDecimal.ZERO;
    }
}

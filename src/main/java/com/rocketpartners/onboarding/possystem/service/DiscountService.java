package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.model.Discount;
import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.DiscountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Service class for Discounts. This class provides methods for computing discounts to apply to a transaction and
 * for computing the total discount amount for a list of discounts.
 */
@ToString
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;

    /**
     * Compute the discounts to apply for the given transaction. The discounts are computed based on the items in the
     * transaction, the customer, and any other relevant information. The list of discounts returned by this method  are
     * not automatically saved to the transaction. The caller is responsible for applying the discounts to the
     * transaction. To compute the discount amount, use {@link #computeDiscountAmountFor(Collection)} with the list
     * returned from this method.
     *
     * @param transaction The transaction for which to compute discounts.
     * @return A list of discounts to apply to the transaction.
     */
    public List<Discount> computeDiscountsToApplyFor(@NonNull Transaction transaction) {
        // TODO: Implement this method
        return new ArrayList<>();
    }

    /**
     * Compute the total discount amount for the given discounts. The total discount amount is the sum of the amounts of
     * all the discounts in the list. The discount amount is computed based on the type of discount and the items in the
     * transaction. The discount amount is not automatically applied to the transaction. The caller is responsible for
     * applying the discount amount to the transaction.
     *
     * @param discounts The discounts for which to compute the total discount amount.
     * @return The total discount amount for the given discounts.
     */
    public BigDecimal computeDiscountAmountFor(@NonNull Collection<Discount> discounts) {
        // TODO: Implement this method
        return BigDecimal.ZERO;
    }
}

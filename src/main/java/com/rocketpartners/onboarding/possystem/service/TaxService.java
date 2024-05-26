package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.model.Transaction;
import lombok.NonNull;

import java.math.BigDecimal;

/**
 * Service class responsible for computing taxes.
 */
public class TaxService {

    /**
     * Compute the taxes for the given transaction. The taxes are computed based on the items in the transaction, the
     * customer, and any other relevant information. The tax rate is fetched from an API source. The tax amount is not
     * automatically applied to the transaction. The caller is responsible for applying the tax amount to the
     * transaction.
     *
     * @param transaction The transaction for which to compute taxes.
     * @return The tax amount for the given transaction.
     */
    public BigDecimal computeTaxesFor(@NonNull Transaction transaction) {
        // TODO: Implement this method to fetch standard tax rate from an API source
        //  for now we will use a fixed tax rate of 4%.
        return transaction.getSubtotal().multiply(BigDecimal.valueOf(0.04));
    }

}

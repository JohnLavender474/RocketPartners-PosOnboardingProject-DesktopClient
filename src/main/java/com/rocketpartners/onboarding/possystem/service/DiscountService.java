package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.commons.model.Discount;
import com.rocketpartners.onboarding.commons.model.DiscountComputation;
import com.rocketpartners.onboarding.commons.model.TransactionDto;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Service class for Discounts. This class provides methods for computing discounts to apply to a transaction and
 * for computing the total discount amount for a list of discounts.
 */
@ToString
public class DiscountService {

    private final WebClient webClient;

    /**
     * Create a new DiscountService with the given base URL.
     *
     * @param baseUrl the base URL
     */
    public DiscountService(@NonNull String baseUrl) {
        webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Get the list of discounts.
     *
     * @return the list of discounts
     */
    public List<Discount> getDiscounts() {
        try {
            return webClient.get().uri("/api/discounts").retrieve().bodyToFlux(Discount.class).collectList().block();
        } catch (Exception e) {
            System.err.println("Failed to get discounts: " + e.getMessage());
            return null;
        }
    }

    /**
     * Compute the discounts to apply to a transaction.
     *
     * @param transaction the transaction dto
     * @return the computed discounts
     */
    public DiscountComputation computeDiscounts(@NonNull TransactionDto transaction) {
        try {
            return webClient.post().uri("/api/discounts/compute").bodyValue(transaction).retrieve()
                    .bodyToMono(DiscountComputation.class).block();
        } catch (Exception e) {
            System.err.println("Failed to compute discounts: " + e.getMessage());
            return null;
        }
    }
}

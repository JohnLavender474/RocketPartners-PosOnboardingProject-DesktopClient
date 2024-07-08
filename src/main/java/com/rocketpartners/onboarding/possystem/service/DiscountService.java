package com.rocketpartners.onboarding.possystem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketpartners.onboarding.commons.model.Discount;
import com.rocketpartners.onboarding.commons.model.DiscountComputation;
import com.rocketpartners.onboarding.commons.model.TransactionDto;
import lombok.NonNull;
import lombok.ToString;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Service class for Discounts. This class provides methods for computing discounts to apply to a transaction and
 * for computing the total discount amount for a list of discounts.
 */
@ToString
public class DiscountService {

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    /**
     * Create a new DiscountService with the given base URL.
     *
     * @param baseUrl the base URL
     */
    public DiscountService(@NonNull String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Get the map of discounts.
     *
     * @return the map of discounts
     * @throws Exception if an error occurs while fetching the discounts
     */
    public @NonNull Map<String, Discount> getDiscounts() throws Exception {
        HttpGet request = new HttpGet(baseUrl + "/api/discounts");
        return httpClient.execute(request, response -> {
            if (response.getCode() != 200) {
                throw new IOException("Unexpected response status: " + response.getCode());
            }

            String json = EntityUtils.toString(response.getEntity());
            Map<String, Discount> discounts = objectMapper.readValue(json, new TypeReference<>() {
            });

            if (discounts == null) {
                throw new RuntimeException("Failed to parse discounts");
            }

            return discounts;
        });
    }

    /**
     * Compute the discounts to apply to a transaction.
     *
     * @param transaction the transaction dto
     * @return the computed discounts, or null if an error occurs
     * @throws Exception if an error occurs while computing the discounts
     */
    public @NonNull DiscountComputation computeDiscounts(@NonNull TransactionDto transaction) throws Exception {
        HttpPost request = new HttpPost(baseUrl + "/api/discounts/compute");
        String json = objectMapper.writeValueAsString(transaction);
        HttpEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        request.setEntity(entity);
        request.setHeader("Content-type", "application/json");

        return httpClient.execute(request, response -> {
            if (response.getCode() != 200) {
                throw new IOException("Unexpected response status: " + response.getCode());
            }

            String responseJson = EntityUtils.toString(response.getEntity());
            DiscountComputation computation = objectMapper.readValue(responseJson, DiscountComputation.class);
            if (computation == null) {
                throw new RuntimeException("Failed to parse discount computation");
            }

            return computation;
        });
    }
}

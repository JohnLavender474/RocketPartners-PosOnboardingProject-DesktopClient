package com.rocketpartners.onboarding.possystem.repository;

import com.rocketpartners.onboarding.possystem.model.Discount;

import java.util.List;

/**
 * Repository for discounts. This interface defines the methods that must be implemented by any class that
 * provides access to discount data. It includes methods for saving, retrieving, and deleting discounts, as well
 * as methods for querying discounts based on their attributes.
 */
public interface DiscountRepository {

    /**
     * Saves the given {@code Discount} to the repository.
     *
     * @param discount the discount to be saved
     */
    void saveDiscount(Discount discount);

    /**
     * Retrieves all {@code Discount} objects from the repository.
     *
     * @return a list of all discounts in the repository
     */
    List<Discount> getAllDiscounts();

    /**
     * Deletes the {@code Discount} with the specified ID from the repository.
     *
     * @param id the ID of the discount to be deleted
     */
    void deleteDiscountById(String id);

    /**
     * Retrieves the {@code Discount} with the specified ID from the repository.
     *
     * @param id the ID of the discount to be retrieved
     * @return the discount with the specified ID, or {@code null} if no such discount exists
     */
    Discount getDiscountById(String id);

    /**
     * Checks whether a {@code Discount} with the specified ID exists in the repository.
     *
     * @param id the ID of the discount to check for
     * @return {@code true} if a discount with the specified ID exists, {@code false} otherwise
     */
    boolean discountExists(String id);

    /**
     * Retrieves a list of {@code Discount} objects from the repository that have the specified type.
     *
     * @param type the type of the discounts to be retrieved
     * @return a list of discounts with the specified type
     */
    List<Discount> getDiscountsByType(String type);

    /**
     * Retrieves a list of {@code Discount} objects from the repository that are applicable to the specified category.
     *
     * @param category the category to which the discounts are applicable
     * @return a list of discounts applicable to the specified category
     */
    List<Discount> getDiscountsByApplicableCategory(String category);

    /**
     * Retrieves a list of {@code Discount} objects from the repository that are applicable to the specified UPC.
     *
     * @param upc the UPC to which the discounts are applicable
     * @return a list of discounts applicable to the specified UPC
     */
    List<Discount> getDiscountsByApplicableUpc(String upc);
}

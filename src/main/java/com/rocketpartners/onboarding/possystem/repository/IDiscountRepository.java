package com.rocketpartners.onboarding.possystem.repository;

import com.rocketpartners.onboarding.possystem.model.Discount;

import java.util.List;

public interface IDiscountRepository {

    void saveDiscount(Discount discount);

    List<Discount> getAllDiscounts();

    void deleteDiscount(Discount discount);

    void deleteDiscountById(String id);

    Discount getDiscountById(String id);

    boolean discountExists(String id);

    List<Discount> getDiscountsByType(String type);

    List<Discount> getDiscountsByApplicableCategory(String category);

    List<Discount> getDiscountsByApplicableUpc(String upc);

}

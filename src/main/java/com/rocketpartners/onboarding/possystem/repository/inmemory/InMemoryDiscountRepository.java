package com.rocketpartners.onboarding.possystem.repository.inmemory;

import com.rocketpartners.onboarding.possystem.model.Discount;
import com.rocketpartners.onboarding.possystem.repository.DiscountRepository;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory implementation of the {@link DiscountRepository} interface.
 */
@ToString
public class InMemoryDiscountRepository implements DiscountRepository {

    private final Map<String, Discount> discounts = new HashMap<>();

    @Override
    public void saveDiscount(Discount discount) {
        if (discount.getId() == null) {
            String id = String.valueOf(discounts.size() + 1);
            discount.setId(id);
        }
        discounts.put(discount.getId(), discount);
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return discounts.values().stream().toList();
    }

    @Override
    public void deleteDiscountById(String id) {
        discounts.remove(id);
    }

    @Override
    public Discount getDiscountById(String id) {
        return discounts.get(id);
    }

    @Override
    public boolean discountExists(String id) {
        return discounts.containsKey(id);
    }

    @Override
    public List<Discount> getDiscountsByType(String type) {
        return discounts.values().stream().filter(discount -> discount.getType().equals(type)).toList();
    }

    @Override
    public List<Discount> getDiscountsByApplicableCategory(String category) {
        return discounts.values().stream().filter(discount -> discount.getApplicableCategory().equals(category)).toList();
    }

    @Override
    public List<Discount> getDiscountsByApplicableUpc(String upc) {
        return discounts.values().stream().filter(discount -> discount.getApplicableUpcs().contains(upc)).toList();
    }
}

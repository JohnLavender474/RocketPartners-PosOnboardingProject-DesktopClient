package com.rocketpartners.onboarding.possystem.repository.inmemory;

import com.rocketpartners.onboarding.possystem.model.Discount;
import com.rocketpartners.onboarding.possystem.repository.DiscountRepository;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
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
    public void saveDiscount(@NonNull Discount discount) {
        if (discount.getId() == null) {
            String id = String.valueOf(discounts.size() + 1);
            discount.setId(id);
        }
        discounts.put(discount.getId(), discount);
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return new ArrayList<>(discounts.values());
    }

    @Override
    public void deleteDiscountById(@NonNull String id) {
        discounts.remove(id);
    }

    @Override
    public Discount getDiscountById(@NonNull String id) {
        return discounts.get(id);
    }

    @Override
    public boolean discountExists(@NonNull String id) {
        return discounts.containsKey(id);
    }

    @Override
    public List<Discount> getDiscountsByType(@NonNull String type) {
        List<Discount> discounts = new ArrayList<>();
        this.discounts.values().forEach(it -> {
            if (type.equals(it.getType())) {
                discounts.add(it);
            }
        });
        return discounts;
    }

    @Override
    public List<Discount> getDiscountsByApplicableCategory(@NonNull String category) {
        List<Discount> discounts = new ArrayList<>();
        this.discounts.values().forEach(it -> {
            if (category.equals(it.getApplicableCategory())) {
                discounts.add(it);
            }
        });
        return discounts;
    }

    @Override
    public List<Discount> getDiscountsByApplicableUpc(@NonNull String upc) {
        List<Discount> discounts = new ArrayList<>();
        this.discounts.values().forEach(it -> {
            if (it.getApplicableUpcs().contains(upc)) {
                discounts.add(it);
            }
        });
        return discounts;
    }
}

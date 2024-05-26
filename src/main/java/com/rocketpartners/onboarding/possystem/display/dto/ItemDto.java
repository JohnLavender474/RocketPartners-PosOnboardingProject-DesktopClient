package com.rocketpartners.onboarding.possystem.display.dto;

import com.rocketpartners.onboarding.possystem.model.Item;
import lombok.*;

import java.math.BigDecimal;

/**
 * ItemDto is a data transfer object that represents an item in the system.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ItemDto {

    @NonNull
    private String upc;
    @NonNull
    private String name;
    @NonNull
    private BigDecimal unitPrice;

    /**
     * Create a new ItemDto from an Item.
     *
     * @param item the item
     * @return the created ItemDto
     */
    public static ItemDto from(@NonNull Item item) {
        return new ItemDto(item.getUpc(), item.getName(), item.getUnitPrice());
    }

    /**
     * Create a new ItemDto from a UPC and a name.
     *
     * @param upc  the UPC
     * @param name the name
     * @return the created ItemDto
     */
    public static ItemDto from(@NonNull String upc, @NonNull String name, @NonNull BigDecimal unitPrice) {
        return new ItemDto(upc, name, unitPrice);
    }
}

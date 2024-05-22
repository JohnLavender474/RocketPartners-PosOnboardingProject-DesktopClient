package com.rocketpartners.onboarding.possystem.display.dto;

import com.rocketpartners.onboarding.possystem.model.Item;
import com.rocketpartners.onboarding.possystem.model.LineItem;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LineItemDto {

    @NonNull
    private String itemUpc;
    @NonNull
    private String itemName;
    @NonNull
    private String transactionId;
    @NonNull
    private BigDecimal unitPrice;
    private int quantity;
    private boolean voided;
    private String category;
    private String description;

    public static LineItemDto from(@NonNull LineItem lineItem, @NonNull Item item) {
        return new LineItemDto(
                lineItem.getItemUpc(),
                item.getName(),
                lineItem.getTransactionId(),
                item.getUnitPrice(),
                lineItem.getQuantity(),
                lineItem.isVoided(),
                item.getCategory(),
                item.getDescription()
        );
    }
}

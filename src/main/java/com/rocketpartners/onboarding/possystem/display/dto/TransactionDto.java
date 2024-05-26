package com.rocketpartners.onboarding.possystem.display.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object for transactions.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class TransactionDto {

    private List<LineItemDto> lineItemDtos;
    private String storeName;
    private int posLane;
    private int transactionNumber;
    private BigDecimal subtotal;
    private BigDecimal discounts;
    private BigDecimal taxes;
    private BigDecimal total;

    /**
     * Create a new TransactionDto with empty line items and zeroed out numbers.
     */
    public TransactionDto() {
        lineItemDtos = new ArrayList<>();
        subtotal = BigDecimal.ZERO;
        discounts = BigDecimal.ZERO;
        taxes = BigDecimal.ZERO;
        total = BigDecimal.ZERO;
    }

    public static TransactionDto from(@NonNull List<LineItemDto> lineItemDtos, @NonNull String storeName,
                                      int posLane, int transactionNumber, @NonNull BigDecimal subtotal,
                                      @NonNull BigDecimal discounts, @NonNull BigDecimal taxes,
                                      @NonNull BigDecimal total) {
        return new TransactionDto(
                lineItemDtos, storeName, posLane, transactionNumber, subtotal, discounts, taxes, total);
    }
}

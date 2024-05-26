package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.possystem.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class TaxServiceTest {

    private TaxService taxService;

    @BeforeEach
    public void setUp() {
        taxService = new TaxService();
    }

    @Test
    public void testComputeTaxesFor() {
        Transaction mockTransaction = Mockito.mock(Transaction.class);

        BigDecimal subtotal = new BigDecimal("100.00");
        when(mockTransaction.getSubtotal()).thenReturn(subtotal);

        BigDecimal expectedTax = subtotal.multiply(BigDecimal.valueOf(0.04));
        BigDecimal actualTax = taxService.computeTaxesFor(mockTransaction);

        assertEquals(expectedTax, actualTax);
    }

    @Test
    public void testComputeTaxesFor_ZeroSubtotal() {
        Transaction mockTransaction = Mockito.mock(Transaction.class);

        BigDecimal subtotal = BigDecimal.ZERO;
        when(mockTransaction.getSubtotal()).thenReturn(subtotal);

        BigDecimal expectedTax = new BigDecimal("0.00");
        BigDecimal actualTax = taxService.computeTaxesFor(mockTransaction);

        assertEquals(expectedTax, actualTax);
    }

    @Test
    public void testComputeTaxesFor_NullSubtotal() {
        Transaction mockTransaction = Mockito.mock(Transaction.class);

        when(mockTransaction.getSubtotal()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            taxService.computeTaxesFor(mockTransaction);
        });
    }
}

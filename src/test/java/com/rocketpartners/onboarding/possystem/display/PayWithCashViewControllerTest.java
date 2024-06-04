package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PayWithCashViewControllerTest {

    private IPosEventDispatcher mockEventDispatcher;
    private PayWithCashView mockPayWithCashView;
    private PayWithCashViewController controller;

    @BeforeEach
    void setUp() {
        mockEventDispatcher = Mockito.mock(IPosEventDispatcher.class);
        mockPayWithCashView = Mockito.mock(PayWithCashView.class);
        controller = new PayWithCashViewController(mockEventDispatcher, mockPayWithCashView);
    }

    @Test
    void testOnPosEventStartPayWithCashProcess() {
        PosEvent event = new PosEvent(PosEventType.START_PAY_WITH_CASH_PROCESS);
        controller.onPosEvent(event);
        verify(mockPayWithCashView, times(1)).setVisible(true);
    }

    @Test
    void testOnPosEventInsufficientFunds() {
        BigDecimal amountNeeded = new BigDecimal("5.00");
        PosEvent event = new PosEvent(PosEventType.INSUFFICIENT_FUNDS,
                Map.of(ConstKeys.AMOUNT_NEEDED, amountNeeded));
        controller.onPosEvent(event);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        String formattedAmountNeeded = currencyFormat.format(amountNeeded);
        verify(mockPayWithCashView, times(1)).notifyInsufficientFunds(formattedAmountNeeded);
    }

    @Test
    void testOnPosEventDoCancelPayment() {
        PosEvent event = new PosEvent(PosEventType.DO_CANCEL_PAYMENT);
        controller.onPosEvent(event);
        verify(mockPayWithCashView, times(1)).clearDisplayAreaText();
        verify(mockPayWithCashView, times(1)).setVisible(false);
    }

    @Test
    void testOnPosEventTransactionCompleted() {
        PosEvent event = new PosEvent(PosEventType.TRANSACTION_COMPLETED);
        controller.onPosEvent(event);
        verify(mockPayWithCashView, times(1)).clearDisplayAreaText();
        verify(mockPayWithCashView, times(1)).setVisible(false);
    }

    @Test
    void testGetEventTypesToListenFor() {
        Set<PosEventType> eventTypes = controller.getEventTypesToListenFor();
        assert eventTypes.contains(PosEventType.START_PAY_WITH_CASH_PROCESS);
        assert eventTypes.contains(PosEventType.INSUFFICIENT_FUNDS);
        assert eventTypes.contains(PosEventType.DO_CANCEL_PAYMENT);
        assert eventTypes.contains(PosEventType.TRANSACTION_COMPLETED);
    }

    @Test
    void testDispatchPosEvent() {
        PosEvent event = new PosEvent(PosEventType.START_PAY_WITH_CASH_PROCESS);
        controller.dispatchPosEvent(event);
        verify(mockEventDispatcher, times(1)).dispatchPosEvent(event);
    }
}


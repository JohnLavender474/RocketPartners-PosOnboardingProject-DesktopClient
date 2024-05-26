package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ScannerViewControllerTest {

    private ScannerView scannerView;
    private IPosEventDispatcher parentPosEventDispatcher;
    private ScannerViewController scannerViewController;

    @BeforeEach
    public void setUp() {
        parentPosEventDispatcher = Mockito.mock(IPosEventDispatcher.class);
        scannerView = Mockito.spy(new ScannerView("Scanner View", parentPosEventDispatcher));
        scannerViewController = new ScannerViewController(parentPosEventDispatcher, scannerView);
    }

    @Test
    public void testTransactionStartedEventActivatesScannerView() {
        PosEvent event = new PosEvent(PosEventType.TRANSACTION_STARTED);
        scannerViewController.onPosEvent(event);
        verify(scannerView, times(1)).setActive();
    }

    @Test
    public void testTransactionCompletedEventDeactivatesScannerView() {
        PosEvent event = new PosEvent(PosEventType.TRANSACTION_COMPLETED);
        scannerViewController.onPosEvent(event);
        verify(scannerView, times(1)).setInactive();
    }

    @Test
    public void testTransactionVoidedEventDeactivatesScannerView() {
        PosEvent event = new PosEvent(PosEventType.TRANSACTION_VOIDED);
        scannerViewController.onPosEvent(event);
        verify(scannerView, times(1)).setInactive();
    }

    @Test
    public void testEnterButtonDispatchesAddItemEvent() {
        when(scannerView.getScannerInput()).thenReturn("1234567890");
        ArgumentCaptor<PosEvent> eventCaptor = ArgumentCaptor.forClass(PosEvent.class);
        scannerView.onEnter();
        verify(parentPosEventDispatcher).dispatchPosEvent(eventCaptor.capture());
        PosEvent capturedEvent = eventCaptor.getValue();
        assertEquals(PosEventType.REQUEST_ADD_ITEM, capturedEvent.getType());
        assertEquals("1234567890", capturedEvent.getProperty(ConstKeys.ITEM_UPC));
    }
}

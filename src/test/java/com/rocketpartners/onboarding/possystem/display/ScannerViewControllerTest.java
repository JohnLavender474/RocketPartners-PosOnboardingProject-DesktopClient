package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ScannerViewControllerTest {

    private ScannerView scannerView;
    private ScannerViewController scannerViewController;

    @BeforeEach
    void setUp() {
        IPosEventDispatcher parentPosEventDispatcher = Mockito.mock(IPosEventDispatcher.class);
        scannerView = Mockito.mock(ScannerView.class);
        scannerViewController = new ScannerViewController(parentPosEventDispatcher, scannerView);
    }

    @Test
    void testTransactionStartedEventActivatesScannerView() {
        PosEvent event = new PosEvent(PosEventType.TRANSACTION_STARTED);
        scannerViewController.onPosEvent(event);
        verify(scannerView, times(1)).setActive();
    }

    @Test
    void testTransactionCompletedEventDeactivatesScannerView() {
        PosEvent event = new PosEvent(PosEventType.TRANSACTION_COMPLETED);
        scannerViewController.onPosEvent(event);
        verify(scannerView, times(1)).setInactive();
    }

    @Test
    void testTransactionVoidedEventDeactivatesScannerView() {
        PosEvent event = new PosEvent(PosEventType.TRANSACTION_VOIDED);
        scannerViewController.onPosEvent(event);
        verify(scannerView, times(1)).setInactive();
    }
}

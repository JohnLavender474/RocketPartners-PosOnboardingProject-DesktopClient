package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ScannerViewControllerTest {

    private ScannerView scannerView;
    private ScannerViewController scannerViewController;

    @BeforeEach
    public void setUp() {
        IPosEventDispatcher parentPosEventDispatcher = Mockito.mock(IPosEventDispatcher.class);
        scannerView = Mockito.mock(ScannerView.class);
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
}

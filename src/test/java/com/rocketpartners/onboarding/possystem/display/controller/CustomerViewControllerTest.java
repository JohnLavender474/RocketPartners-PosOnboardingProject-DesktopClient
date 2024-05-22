package com.rocketpartners.onboarding.possystem.display.controller;

import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.display.view.CustomerView;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CustomerViewControllerTest {

    private CustomerView customerViewMock;
    private CustomerViewController customerViewController;

    @BeforeEach
    public void setUp() {
        IPosEventDispatcher posEventDispatcherMock = mock(IPosEventDispatcher.class);
        customerViewMock = mock(CustomerView.class);
        customerViewController = new CustomerViewController(posEventDispatcherMock, customerViewMock);
    }

    @Test
    public void testOnPosEvent_PosBootup() {
        PosEvent posEvent = new PosEvent(PosEventType.POS_BOOTUP);
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).showTransactionNotStarted();
        assertEquals(TransactionState.NOT_STARTED, customerViewController.getTransactionState());
    }

    @Test
    public void testOnPosEvent_TransactionStarted() {
        PosEvent posEvent = new PosEvent(PosEventType.TRANSACTION_STARTED);
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).showScanningInProgress();
        assertEquals(TransactionState.SCANNING_IN_PROGRESS, customerViewController.getTransactionState());
    }

    @Test
    public void testOnPosEvent_TransactionVoided() {
        PosEvent posEvent = new PosEvent(PosEventType.TRANSACTION_VOIDED);
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).showTransactionVoided();
        assertEquals(TransactionState.VOIDED, customerViewController.getTransactionState());
    }

    @Test
    public void testOnPosEvent_TransactionCompleted() {
        PosEvent posEvent = new PosEvent(PosEventType.TRANSACTION_COMPLETED);
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).showTransactionCompleted();
        assertEquals(TransactionState.COMPLETED, customerViewController.getTransactionState());
    }

    @Test
    public void testSetTransactionState_NotStarted() {
        customerViewController.setTransactionState(TransactionState.NOT_STARTED);
        verify(customerViewMock).showTransactionNotStarted();
        assertEquals(TransactionState.NOT_STARTED, customerViewController.getTransactionState());
    }

    @Test
    public void testSetTransactionState_ScanningInProgress() {
        customerViewController.setTransactionState(TransactionState.SCANNING_IN_PROGRESS);
        verify(customerViewMock).showScanningInProgress();
        assertEquals(TransactionState.SCANNING_IN_PROGRESS, customerViewController.getTransactionState());
    }

    @Test
    public void testSetTransactionState_Voided() {
        customerViewController.setTransactionState(TransactionState.VOIDED);
        verify(customerViewMock).showTransactionVoided();
        assertEquals(TransactionState.VOIDED, customerViewController.getTransactionState());
    }

    @Test
    public void testSetTransactionState_Completed() {
        customerViewController.setTransactionState(TransactionState.COMPLETED);
        verify(customerViewMock).showTransactionCompleted();
        assertEquals(TransactionState.COMPLETED, customerViewController.getTransactionState());
    }

    @Test
    public void testBootUp() {
        customerViewController.bootUp();
        verify(customerViewMock).setVisible(true);
    }
}


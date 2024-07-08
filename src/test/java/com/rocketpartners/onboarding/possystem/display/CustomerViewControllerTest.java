package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.commons.model.ItemDto;
import com.rocketpartners.onboarding.commons.model.LineItemDto;
import com.rocketpartners.onboarding.commons.model.TransactionDto;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CustomerViewControllerTest {

    private CustomerView customerViewMock;
    private CustomerViewController customerViewController;

    @BeforeEach
    void setUp() {
        IPosEventDispatcher posEventDispatcherMock = mock(IPosEventDispatcher.class);
        customerViewMock = mock(CustomerView.class);
        customerViewController = new CustomerViewController(posEventDispatcherMock, customerViewMock);
    }

    @Test
    void testOnPosEvent_PosBootup() {
        PosEvent posEvent = new PosEvent(PosEventType.POS_BOOTUP);
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).showTransactionNotStarted();
    }

    @Test
    void testOnPosEvent_TransactionStarted() {
        PosEvent posEvent = new PosEvent(PosEventType.TRANSACTION_STARTED,
                Map.of(ConstKeys.TRANSACTION_NUMBER, 1));
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).updateTransactionNumber(1);
        verify(customerViewMock).showScanningInProgress();
    }

    @Test
    void testOnPosEvent_TransactionVoided() {
        PosEvent posEvent = new PosEvent(PosEventType.TRANSACTION_VOIDED);
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).showTransactionVoided();
    }

    @Test
    void testOnPosEvent_TransactionCompleted() {
        PosEvent posEvent = new PosEvent(PosEventType.TRANSACTION_COMPLETED);
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).showTransactionCompleted();
    }

    @Test
    void testOnPosEvent_DoUpdateQuickItems() {
        List<ItemDto> itemDtos = List.of(new ItemDto("item1", "description1", BigDecimal.ONE));
        PosEvent posEvent = new PosEvent(PosEventType.DO_UPDATE_QUICK_ITEMS,
                Map.of(ConstKeys.ITEM_DTOS, itemDtos));
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).updateQuickItems(itemDtos);
    }

    @Test
    void testOnPosEvent_ItemAdded() {
        TransactionDto transactionDto = new TransactionDto();
        LineItemDto lineItemDto = new LineItemDto();
        lineItemDto.setItemUpc("item1");
        List<LineItemDto> lineItemDtos = List.of(lineItemDto);
        transactionDto.setLineItemDtos(lineItemDtos);

        PosEvent posEvent = new PosEvent(PosEventType.ITEM_ADDED,
                Map.of(ConstKeys.TRANSACTION_DTO, transactionDto));
        customerViewController.onPosEvent(posEvent);

        verify(customerViewMock).updateTransactionsTable(lineItemDtos);
    }

    @Test
    void testOnPosEvent_ItemRemoved() {
        TransactionDto transactionDto = new TransactionDto();
        LineItemDto lineItemDto = new LineItemDto();
        lineItemDto.setItemUpc("item1");
        List<LineItemDto> lineItemDtos = List.of(lineItemDto);
        transactionDto.setLineItemDtos(lineItemDtos);

        PosEvent posEvent = new PosEvent(PosEventType.ITEM_REMOVED,
                Map.of(ConstKeys.TRANSACTION_DTO, transactionDto));
        customerViewController.onPosEvent(posEvent);

        verify(customerViewMock).updateTransactionsTable(lineItemDtos);
    }

    @Test
    void testOnPosEvent_LineItemsVoided() {
        TransactionDto transactionDto = new TransactionDto();
        LineItemDto lineItemDto = new LineItemDto();
        lineItemDto.setItemUpc("item1");
        List<LineItemDto> lineItemDtos = List.of(lineItemDto);
        transactionDto.setLineItemDtos(lineItemDtos);

        PosEvent posEvent = new PosEvent(PosEventType.LINE_ITEMS_VOIDED,
                Map.of(ConstKeys.TRANSACTION_DTO, transactionDto));
        customerViewController.onPosEvent(posEvent);

        verify(customerViewMock).updateTransactionsTable(lineItemDtos);
    }

    @Test
    void testOnPosEvent_StartPayWithCardProcess() {
        PosEvent posEvent = new PosEvent(PosEventType.START_PAY_WITH_CARD_PROCESS);
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).showAwaitingPayment();
    }

    @Test
    void testOnPosEvent_StartPayWithCashProcess() {
        PosEvent posEvent = new PosEvent(PosEventType.START_PAY_WITH_CASH_PROCESS);
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).showAwaitingPayment();
    }

    @Test
    void testSetTransactionState_NotStarted() {
        customerViewController.setTransactionState(TransactionState.NOT_STARTED);
        verify(customerViewMock).showTransactionNotStarted();
    }

    @Test
    void testSetTransactionState_ScanningInProgress() {
        customerViewController.setTransactionState(TransactionState.SCANNING_IN_PROGRESS);
        verify(customerViewMock).showScanningInProgress();
    }

    @Test
    void testSetTransactionState_Voided() {
        customerViewController.setTransactionState(TransactionState.VOIDED);
        verify(customerViewMock).showTransactionVoided();
    }

    @Test
    void testSetTransactionState_Completed() {
        customerViewController.setTransactionState(TransactionState.COMPLETED);
        verify(customerViewMock).showTransactionCompleted();
    }

    @Test
    void testBootUp() {
        customerViewController.bootUp();
        verify(customerViewMock).setVisible(true);
    }
}


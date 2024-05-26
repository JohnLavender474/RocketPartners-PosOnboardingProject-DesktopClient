package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.display.dto.ItemDto;
import com.rocketpartners.onboarding.possystem.display.dto.LineItemDto;
import com.rocketpartners.onboarding.possystem.display.dto.TransactionDto;
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
    }

    @Test
    public void testOnPosEvent_TransactionStarted() {
        PosEvent posEvent = new PosEvent(PosEventType.TRANSACTION_STARTED,
                Map.of(ConstKeys.TRANSACTION_NUMBER, 1));
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).updateTransactionNumber(1);
        verify(customerViewMock).showScanningInProgress();
    }

    @Test
    public void testOnPosEvent_TransactionVoided() {
        PosEvent posEvent = new PosEvent(PosEventType.TRANSACTION_VOIDED);
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).showTransactionVoided();
    }

    @Test
    public void testOnPosEvent_TransactionCompleted() {
        PosEvent posEvent = new PosEvent(PosEventType.TRANSACTION_COMPLETED);
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).showTransactionCompleted();
    }

    @Test
    public void testOnPosEvent_DoUpdateQuickItems() {
        List<ItemDto> itemDtos = List.of(new ItemDto("item1", "description1", BigDecimal.ONE));
        PosEvent posEvent = new PosEvent(PosEventType.DO_UPDATE_QUICK_ITEMS,
                Map.of(ConstKeys.ITEM_DTOS, itemDtos));
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).updateQuickItems(itemDtos);
    }

    @Test
    public void testOnPosEvent_ItemAdded() {
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
    public void testOnPosEvent_ItemRemoved() {
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
    public void testOnPosEvent_LineItemsVoided() {
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
    public void testOnPosEvent_StartPayWithCardProcess() {
        PosEvent posEvent = new PosEvent(PosEventType.START_PAY_WITH_CARD_PROCESS);
        customerViewController.onPosEvent(posEvent);
        verify(customerViewMock).showAwaitingPayment();
    }


    @Test
    public void testSetTransactionState_NotStarted() {
        customerViewController.setTransactionState(TransactionState.NOT_STARTED);
        verify(customerViewMock).showTransactionNotStarted();
    }

    @Test
    public void testSetTransactionState_ScanningInProgress() {
        customerViewController.setTransactionState(TransactionState.SCANNING_IN_PROGRESS);
        verify(customerViewMock).showScanningInProgress();
    }

    @Test
    public void testSetTransactionState_Voided() {
        customerViewController.setTransactionState(TransactionState.VOIDED);
        verify(customerViewMock).showTransactionVoided();
    }

    @Test
    public void testSetTransactionState_Completed() {
        customerViewController.setTransactionState(TransactionState.COMPLETED);
        verify(customerViewMock).showTransactionCompleted();
    }

    @Test
    public void testBootUp() {
        customerViewController.bootUp();
        verify(customerViewMock).setVisible(true);
    }
}


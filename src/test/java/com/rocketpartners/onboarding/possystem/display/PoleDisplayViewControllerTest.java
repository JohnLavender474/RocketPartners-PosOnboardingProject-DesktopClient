package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.display.dto.ItemDto;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class PoleDisplayViewControllerTest {

    private PoleDisplayView mockPoleDisplayView;
    private PoleDisplayViewController controller;

    @BeforeEach
    public void setUp() {
        mockPoleDisplayView = Mockito.mock(PoleDisplayView.class);
        controller = new PoleDisplayViewController(mockPoleDisplayView);
    }

    @Test
    public void testOnPosEvent_TransactionStarted() {
        PosEvent event = new PosEvent(PosEventType.TRANSACTION_STARTED);

        controller.onPosEvent(event);

        verify(mockPoleDisplayView).clearItems();
        verify(mockPoleDisplayView).setVisible(true);
    }

    @Test
    public void testOnPosEvent_DoOpenPoleDisplay() {
        PosEvent event = new PosEvent(PosEventType.DO_OPEN_POLE_DISPLAY);

        controller.onPosEvent(event);

        verify(mockPoleDisplayView).setVisible(true);
    }

    @Test
    public void testOnPosEvent_ItemAdded() {
        ItemDto mockItemDto = Mockito.mock(ItemDto.class);
        PosEvent event = new PosEvent(PosEventType.ITEM_ADDED,
                Map.of(ConstKeys.ITEM_DTO, mockItemDto));

        controller.onPosEvent(event);

        verify(mockPoleDisplayView).addItem(mockItemDto);
    }

    @Test
    public void testOnPosEvent_ItemRemoved() {
        ItemDto mockItemDto = Mockito.mock(ItemDto.class);
        PosEvent event = new PosEvent(PosEventType.ITEM_REMOVED,
                Map.of(ConstKeys.ITEM_DTO, mockItemDto));

        controller.onPosEvent(event);

        verify(mockPoleDisplayView).removeItem(mockItemDto);
    }

    @Test
    public void testGetEventTypesToListenFor() {
        Set<PosEventType> eventTypes = controller.getEventTypesToListenFor();

        assertEquals(Set.of(
                PosEventType.TRANSACTION_STARTED,
                PosEventType.DO_OPEN_POLE_DISPLAY,
                PosEventType.ITEM_ADDED,
                PosEventType.ITEM_REMOVED
        ), eventTypes);
    }
}


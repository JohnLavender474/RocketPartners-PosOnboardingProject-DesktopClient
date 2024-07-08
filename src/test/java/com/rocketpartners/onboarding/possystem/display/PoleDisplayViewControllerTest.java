package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.commons.model.ItemDto;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class PoleDisplayViewControllerTest {

    private PoleDisplayView mockPoleDisplayView;
    private PoleDisplayViewController controller;

    @BeforeEach
    void setUp() {
        mockPoleDisplayView = Mockito.mock(PoleDisplayView.class);
        controller = new PoleDisplayViewController(mockPoleDisplayView);
    }

    @Test
    void testOnPosEvent_TransactionStarted() {
        PosEvent event = new PosEvent(PosEventType.TRANSACTION_STARTED);

        controller.onPosEvent(event);

        verify(mockPoleDisplayView).clearItems();
        verify(mockPoleDisplayView).setVisible(true);
    }

    @Test
    void testOnPosEvent_DoOpenPoleDisplay() {
        PosEvent event = new PosEvent(PosEventType.DO_OPEN_POLE_DISPLAY);

        controller.onPosEvent(event);

        verify(mockPoleDisplayView).setVisible(true);
    }

    @Test
    void testOnPosEvent_ItemAdded() {
        ItemDto mockItemDto = Mockito.mock(ItemDto.class);
        PosEvent event = new PosEvent(PosEventType.ITEM_ADDED,
                Map.of(ConstKeys.ITEM_DTO, mockItemDto));

        controller.onPosEvent(event);

        verify(mockPoleDisplayView).addItem(mockItemDto);
    }

    @Test
    void testOnPosEvent_ItemRemoved() {
        ItemDto mockItemDto = Mockito.mock(ItemDto.class);
        PosEvent event = new PosEvent(PosEventType.ITEM_REMOVED,
                Map.of(ConstKeys.ITEM_DTO, mockItemDto));

        controller.onPosEvent(event);

        verify(mockPoleDisplayView).removeItem(mockItemDto);
    }

    @Test
    void testGetEventTypesToListenFor() {
        Set<PosEventType> eventTypes = controller.getEventTypesToListenFor();

        assertEquals(Set.of(
                PosEventType.TRANSACTION_STARTED,
                PosEventType.DO_OPEN_POLE_DISPLAY,
                PosEventType.ITEM_ADDED,
                PosEventType.ITEM_REMOVED
        ), eventTypes);
    }
}


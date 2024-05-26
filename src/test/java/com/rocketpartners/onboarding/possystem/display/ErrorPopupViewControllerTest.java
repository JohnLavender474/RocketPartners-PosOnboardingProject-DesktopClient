package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ErrorPopupViewControllerTest {

    private ErrorPopupViewController controller;

    @BeforeEach
    public void setUp() {
        controller = new ErrorPopupViewController();
    }

    @Test
    public void testGetEventTypesToListenFor() {
        Set<PosEventType> eventTypes = controller.getEventTypesToListenFor();

        assertEquals(Set.of(PosEventType.ERROR), eventTypes);
    }

    @Test
    public void testOnPosEvent_Error() {
        PosEvent event = Mockito.mock(PosEvent.class);
        when(event.getType()).thenReturn(PosEventType.ERROR);
        when(event.getProperty(ConstKeys.ERROR, String.class)).thenReturn("Test Error");

        MockedStatic<JOptionPane> mockedJOptionPane = mockStatic(JOptionPane.class);

        controller.onPosEvent(event);

        mockedJOptionPane.verify(() -> JOptionPane.showMessageDialog(
                        null, "Test Error", "Error", JOptionPane.ERROR_MESSAGE),
                times(1)
        );

        mockedJOptionPane.close();
    }

    @Test
    public void testDispatchPosEvent() {
        PosEvent event = Mockito.mock(PosEvent.class);
        controller.dispatchPosEvent(event);
        verifyNoInteractions(event);
    }
}

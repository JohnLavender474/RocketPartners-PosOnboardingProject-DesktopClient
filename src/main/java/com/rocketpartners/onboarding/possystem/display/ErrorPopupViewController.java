package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.component.IComponent;
import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import javax.swing.*;
import java.util.Set;

/**
 * A controller that listens for {@link PosEventType#ERROR} events and displays an error popup dialog with the error.
 * This controller class does not own any UI components but rather listens for events and displays a popup dialog when
 * an error event is received.
 */
public class ErrorPopupViewController implements IPosEventDispatcher, IPosEventListener {

    private static final Set<PosEventType> eventTypesToListenFor = Set.of(
            PosEventType.ERROR
    );

    @Override
    public @NonNull Set<PosEventType> getEventTypesToListenFor() {
        return eventTypesToListenFor;
    }

    @Override
    public void onPosEvent(@NonNull PosEvent event) {
        if (event.getType() == PosEventType.ERROR) {
            String error = event.getProperty(ConstKeys.ERROR, String.class);
            JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void dispatchPosEvent(PosEvent event) {
        // do nothing
    }
}

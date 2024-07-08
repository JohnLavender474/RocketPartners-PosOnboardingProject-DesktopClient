package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
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
public class ErrorPopupViewController implements IPosEventListener {

    private static final Set<PosEventType> eventTypesToListenFor = Set.of(PosEventType.ERROR);

    @Override
    public @NonNull Set<PosEventType> getEventTypesToListenFor() {
        return eventTypesToListenFor;
    }

    @Override
    public void onPosEvent(@NonNull PosEvent event) {
        if (event.getType() == PosEventType.ERROR) {
            String error = event.getProperty(ConstKeys.MESSAGE, String.class);
            JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

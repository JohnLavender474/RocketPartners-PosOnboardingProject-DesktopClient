package com.rocketpartners.onboarding.possystem.component.journal;

import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import com.rocketpartners.onboarding.possystem.utils.LogFormatter;
import lombok.NonNull;

import java.util.EnumSet;
import java.util.Set;

/**
 * A simple journal listener that writes {@link PosEventType#LOG} and {@link PosEventType#ERROR} event logs to the
 * command line.
 */
public class LocalJournal implements IPosEventListener {

    @Override
    public @NonNull Set<PosEventType> getEventTypesToListenFor() {
        return EnumSet.of(PosEventType.LOG, PosEventType.ERROR);
    }

    @Override
    public void onPosEvent(@NonNull PosEvent event) {
        String message = event.getProperty("message", String.class);
        switch (event.getType()) {
            case LOG:
                System.out.println(LogFormatter.formatLog(message));
                break;
            case ERROR:
                System.err.println(LogFormatter.formatError(message));
                break;
        }
    }
}

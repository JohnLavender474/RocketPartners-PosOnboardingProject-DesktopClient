package com.rocketpartners.onboarding.possystem.component.journal;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * A simple journal listener that writes to the command line. This is useful for debugging. It can be used to write
 * log entries, debug entries, and error messages. The debug entries are only written if the debug flag is set to true.
 */
@AllArgsConstructor
public class CommandLineJournalListener implements IPosJournalListener {

    private boolean debug;

    @Override
    public void onLog(@NonNull String entry) {
        System.out.println(entry);
    }

    @Override
    public void onDebug(@NonNull String entry) {
        if (debug) {
            System.out.println(entry);
        }
    }

    @Override
    public void onError(@NonNull String entry) {
        System.err.println(entry);
    }
}

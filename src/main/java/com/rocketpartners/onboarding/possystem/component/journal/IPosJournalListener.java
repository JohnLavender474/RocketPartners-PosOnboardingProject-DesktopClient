package com.rocketpartners.onboarding.possystem.component.journal;

import lombok.NonNull;

/**
 * Listener interface for journal entries.
 */
public interface IPosJournalListener {

    /**
     * Called when a journal entry is logged.
     *
     * @param entry The journal entry.
     */
    void onLog(@NonNull String entry);

    /**
     * Called when a debug entry is logged.
     *
     * @param entry The debug entry.
     */
    void onDebug(@NonNull String entry);

    /**
     * Called when an error occurs.
     *
     * @param entry The error message.
     */
    void onError(@NonNull String entry);
}

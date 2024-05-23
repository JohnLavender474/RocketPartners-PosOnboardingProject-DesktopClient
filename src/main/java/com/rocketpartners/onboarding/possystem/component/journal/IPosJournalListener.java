package com.rocketpartners.onboarding.possystem.component.journal;

/**
 * Listener interface for journal entries.
 */
public interface IPosJournalListener {

    /**
     * Called when a journal entry is logged.
     *
     * @param entry The journal entry.
     */
    void onLog(String entry);

    /**
     * Called when an error occurs.
     *
     * @param entry The error message.
     */
    void onError(String entry);
}

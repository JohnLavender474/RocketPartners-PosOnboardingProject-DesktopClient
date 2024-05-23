package com.rocketpartners.onboarding.possystem.component.journal;

import com.rocketpartners.onboarding.possystem.component.IComponent;

import java.util.HashSet;
import java.util.Set;

/**
 * Component that logs journal entries.
 */
public class PosJournalComponent implements IComponent {

    private final Set<IPosJournalListener> journalListeners;

    /**
     * Creates a new journal component.
     */
    public PosJournalComponent() {
        journalListeners = new HashSet<>();
    }

    /**
     * Adds a journal listener.
     *
     * @param listener The listener to add.
     */
    public void addJournalListener(IPosJournalListener listener) {
        journalListeners.add(listener);
    }

    /**
     * Removes a journal listener.
     *
     * @param listener The listener to remove.
     */
    public void removeJournalListener(IPosJournalListener listener) {
        journalListeners.remove(listener);
    }

    /**
     * Logs a journal entry.
     *
     * @param entry The journal entry.
     */
    public void log(String entry) {
        journalListeners.forEach(listener -> listener.onLog(entry));
    }

    /**
     * Logs a debug entry.
     *
     * @param entry The debug entry.
     */
    public void debug(String entry) {
        journalListeners.forEach(listener -> listener.onDebug(entry));
    }

    /**
     * Logs an error.
     *
     * @param entry The error message.
     */
    public void error(String entry) {
        journalListeners.forEach(listener -> listener.onError(entry));
    }
}

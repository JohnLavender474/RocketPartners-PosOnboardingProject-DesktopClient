package com.rocketpartners.onboarding.possystem.display.controller;

import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.display.view.ScannerView;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.EnumSet;
import java.util.Set;

/**
 * Controller for the scanner view. This class is responsible for updating the scanner view based on POS events.
 * The scanner view is created with the parent POS event dispatcher.
 */
public class ScannerViewController implements IController {

    private static final Set<PosEventType> eventsToListenFor = EnumSet.of(
            PosEventType.POS_BOOTUP,
            PosEventType.POS_RESET,
            PosEventType.TRANSACTION_STARTED,
            PosEventType.TRANSACTION_VOIDED,
            PosEventType.TRANSACTION_COMPLETED,
            PosEventType.REQUEST_ADD_ITEM,
            PosEventType.ADD_ITEM
    );

    @NonNull
    private final IPosEventDispatcher parentPosEventDispatcher;
    @NonNull
    private final ScannerView scannerView;
    @NonNull
    @Getter
    @Setter
    private TransactionState transactionState;

    /**
     * Constructor that accepts a parent POS event dispatcher. The transaction state is set to NOT_STARTED and will be
     * updated when the POS system sends an event contained in {@link #getEventTypesToListenFor()}.
     *
     * @param parentPosEventDispatcher The parent POS event dispatcher.
     */
    public ScannerViewController(@NonNull IPosEventDispatcher parentPosEventDispatcher) {
        this.parentPosEventDispatcher = parentPosEventDispatcher;
        scannerView = new ScannerView(this);
        transactionState = TransactionState.NOT_STARTED;
    }

    /**
     * Constructor that accepts a parent POS event dispatcher. The transaction state is set to NOT_STARTED and will be
     * updated when the POS system sends an event contained in {@link #getEventTypesToListenFor()}. Package-private for
     * testing.
     *
     * @param parentPosEventDispatcher The parent POS event dispatcher.
     * @param scannerView The scanner view.
     */
    ScannerViewController(@NonNull IPosEventDispatcher parentPosEventDispatcher, @NonNull ScannerView scannerView) {
        this.parentPosEventDispatcher = parentPosEventDispatcher;
        this.scannerView = scannerView;
        transactionState = TransactionState.NOT_STARTED;
    }

    @Override
    public @NonNull Set<PosEventType> getEventTypesToListenFor() {
        return eventsToListenFor;
    }

    @Override
    public void dispatchPosEvent(@NonNull PosEvent event) {
        parentPosEventDispatcher.dispatchPosEvent(event);
    }

    @Override
    public void onPosEvent(@NonNull PosEvent event) {
        switch (event.getType()) {
            case TRANSACTION_STARTED -> scannerView.setActive();
            case TRANSACTION_VOIDED, TRANSACTION_COMPLETED -> scannerView.setInactive();
        }
    }

    @Override
    public void shutdown() {
        scannerView.setVisible(false);
    }
}

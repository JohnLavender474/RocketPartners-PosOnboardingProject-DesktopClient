package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.component.IComponent;
import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.awt.*;
import java.util.EnumSet;
import java.util.Set;

/**
 * Controller for the scanner view. This class is responsible for updating the scanner view based on POS events.
 * The scanner view is created with the parent POS event dispatcher.
 */
public class ScannerViewController implements IPosEventDispatcher, IPosEventListener {

    private static final Set<PosEventType> eventsToListenFor = EnumSet.of(
            PosEventType.POS_BOOTUP,
            PosEventType.POS_RESET,
            PosEventType.DO_OPEN_SCANNER,
            PosEventType.TRANSACTION_STARTED,
            PosEventType.TRANSACTION_VOIDED,
            PosEventType.TRANSACTION_COMPLETED
    );

    @NonNull
    private final IPosEventDispatcher parentPosEventDispatcher;
    @NonNull
    @Getter(AccessLevel.PACKAGE)
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
    public ScannerViewController(@NonNull String frameTitle, @NonNull IPosEventDispatcher parentPosEventDispatcher) {
        this.parentPosEventDispatcher = parentPosEventDispatcher;
        scannerView = new ScannerView(frameTitle, this);
        transactionState = TransactionState.NOT_STARTED;
    }

    /**
     * Constructor that accepts a parent POS event dispatcher. The transaction state is set to NOT_STARTED and will be
     * updated when the POS system sends an event contained in {@link #getEventTypesToListenFor()}. Package-private for
     * testing.
     *
     * @param parentPosEventDispatcher The parent POS event dispatcher.
     * @param scannerView              The scanner view.
     */
    ScannerViewController(@NonNull IPosEventDispatcher parentPosEventDispatcher, @NonNull ScannerView scannerView) {
        this.parentPosEventDispatcher = parentPosEventDispatcher;
        this.scannerView = scannerView;
        transactionState = TransactionState.NOT_STARTED;
    }

    /**
     * Add the scanner view to the keyboard focus manager.
     *
     * @param keyboardFocusManager The keyboard focus manager.
     */
    public void addScannerViewKeyboardFocusManager(@NonNull KeyboardFocusManager keyboardFocusManager) {
        keyboardFocusManager.addKeyEventDispatcher(scannerView);
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
            case DO_OPEN_SCANNER -> {
                if (Application.DEBUG) {
                    System.out.println("[ScannerViewController] Received DO_OPEN_SCANNER event");
                }
                scannerView.setVisible(true);
                scannerView.requestUserFocus();
            }
            case POS_BOOTUP, POS_RESET, TRANSACTION_STARTED -> {
                scannerView.setVisible(true);
                scannerView.setActive();
            }
            case TRANSACTION_VOIDED, TRANSACTION_COMPLETED -> scannerView.setInactive();
        }
    }
}

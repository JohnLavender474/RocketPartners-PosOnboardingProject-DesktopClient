package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import java.util.Set;

/**
 * Controller for the keypad view. This class is responsible for updating the keypad view based on POS events.
 */
public class KeypadViewController implements IController {

    private static final Set<PosEventType> eventTypesToListenFor = Set.of(
            PosEventType.START_PAY_WITH_CARD_PROCESS,
            PosEventType.DO_CANCEL_PAYMENT,
            PosEventType.TRANSACTION_COMPLETED
    );

    private final IPosEventDispatcher parentPosEventDispatcher;
    private final KeypadView keypadView;

    /**
     * Constructor that accepts a frame title and a parent POS event dispatcher.
     *
     * @param frameTitle               The title of the frame.
     * @param parentPosEventDispatcher The parent POS event dispatcher.
     */
    public KeypadViewController(@NonNull String frameTitle, @NonNull IPosEventDispatcher parentPosEventDispatcher) {
        this.parentPosEventDispatcher = parentPosEventDispatcher;
        keypadView = new KeypadView(frameTitle, this);
    }

    /**
     * Package-private constructor for testing.
     *
     * @param parentPosEventDispatcher The parent POS event dispatcher.
     * @param keypadView               The keypad view.
     */
    KeypadViewController(@NonNull IPosEventDispatcher parentPosEventDispatcher,
                         @NonNull KeypadView keypadView) {
        this.parentPosEventDispatcher = parentPosEventDispatcher;
        this.keypadView = keypadView;
    }

    @Override
    public void onPosEvent(@NonNull PosEvent event) {
        switch (event.getType()) {
            case START_PAY_WITH_CARD_PROCESS -> keypadView.setVisible(true);
            case DO_CANCEL_PAYMENT, TRANSACTION_COMPLETED -> {
                keypadView.clearDisplayArea();
                keypadView.setVisible(false);
            }
        }
    }

    @Override
    public @NonNull Set<PosEventType> getEventTypesToListenFor() {
        return eventTypesToListenFor;
    }

    @Override
    public void dispatchPosEvent(@NonNull PosEvent event) {
        parentPosEventDispatcher.dispatchPosEvent(event);
    }
}

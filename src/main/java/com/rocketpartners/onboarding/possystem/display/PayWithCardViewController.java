package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.component.IComponent;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import java.util.Set;

/**
 * Controller for the keypad view. This class is responsible for updating the keypad view based on POS events.
 */
public class PayWithCardViewController implements IPosEventDispatcher, IPosEventListener {

    private static final Set<PosEventType> eventTypesToListenFor = Set.of(
            PosEventType.START_PAY_WITH_CARD_PROCESS,
            PosEventType.DO_CANCEL_PAYMENT,
            PosEventType.TRANSACTION_COMPLETED
    );

    private final IPosEventDispatcher parentPosEventDispatcher;
    private final PayWithCardView payWithCardView;

    /**
     * Constructor that accepts a frame title and a parent POS event dispatcher.
     *
     * @param frameTitle               The title of the frame.
     * @param parentPosEventDispatcher The parent POS event dispatcher.
     */
    public PayWithCardViewController(@NonNull String frameTitle, @NonNull IPosEventDispatcher parentPosEventDispatcher) {
        this.parentPosEventDispatcher = parentPosEventDispatcher;
        payWithCardView = new PayWithCardView(frameTitle, this);
    }

    /**
     * Package-private constructor for testing.
     *
     * @param parentPosEventDispatcher The parent POS event dispatcher.
     * @param payWithCardView               The keypad view.
     */
    PayWithCardViewController(@NonNull IPosEventDispatcher parentPosEventDispatcher,
                              @NonNull PayWithCardView payWithCardView) {
        this.parentPosEventDispatcher = parentPosEventDispatcher;
        this.payWithCardView = payWithCardView;
    }

    @Override
    public void onPosEvent(@NonNull PosEvent event) {
        switch (event.getType()) {
            case START_PAY_WITH_CARD_PROCESS -> payWithCardView.setVisible(true);
            case DO_CANCEL_PAYMENT, TRANSACTION_COMPLETED -> {
                payWithCardView.clearDisplayAreaText();
                payWithCardView.setVisible(false);
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

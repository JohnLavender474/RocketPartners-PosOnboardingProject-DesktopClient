package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.component.IComponent;
import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Set;

/**
 * Controller for the keypad view. This class is responsible for updating the keypad view based on POS events.
 */
public class PayWithCashViewController implements IPosEventDispatcher, IPosEventListener {

    private static final Set<PosEventType> eventTypesToListenFor = Set.of(
            PosEventType.START_PAY_WITH_CASH_PROCESS,
            PosEventType.INSUFFICIENT_FUNDS,
            PosEventType.DO_CANCEL_PAYMENT,
            PosEventType.TRANSACTION_COMPLETED
    );

    private final IPosEventDispatcher parentEventDispatcher;
    private final PayWithCashView payWithCashView;

    /**
     * Constructor that accepts a frame title and a parent POS event dispatcher.
     *
     * @param frameTitle            The title of the frame.
     * @param parentEventDispatcher The parent POS event dispatcher.
     */
    public PayWithCashViewController(@NonNull String frameTitle, @NonNull IPosEventDispatcher parentEventDispatcher) {
        this.parentEventDispatcher = parentEventDispatcher;
        payWithCashView = new PayWithCashView(frameTitle, this);
    }

    /**
     * Package-private constructor for testing.
     *
     * @param parentEventDispatcher The parent POS event dispatcher.
     * @param payWithCashView       The keypad view.
     */
    PayWithCashViewController(@NonNull IPosEventDispatcher parentEventDispatcher,
                              @NonNull PayWithCashView payWithCashView) {
        this.parentEventDispatcher = parentEventDispatcher;
        this.payWithCashView = payWithCashView;
    }

    @Override
    public void onPosEvent(@NonNull PosEvent event) {
        switch (event.getType()) {
            case START_PAY_WITH_CASH_PROCESS -> payWithCashView.setVisible(true);
            case INSUFFICIENT_FUNDS -> {
                BigDecimal amountNeeded = event.getProperty(ConstKeys.AMOUNT_NEEDED, BigDecimal.class);
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
                String formattedAmountNeeded = currencyFormat.format(amountNeeded);
                payWithCashView.notifyInsufficientFunds(formattedAmountNeeded);
            }
            case DO_CANCEL_PAYMENT, TRANSACTION_COMPLETED -> {
                payWithCashView.clearDisplayAreaText();
                payWithCashView.setVisible(false);
            }
        }
    }

    @Override
    public void dispatchPosEvent(@NonNull PosEvent event) {
        parentEventDispatcher.dispatchPosEvent(event);
    }

    @Override
    public @NonNull Set<PosEventType> getEventTypesToListenFor() {
        return eventTypesToListenFor;
    }
}

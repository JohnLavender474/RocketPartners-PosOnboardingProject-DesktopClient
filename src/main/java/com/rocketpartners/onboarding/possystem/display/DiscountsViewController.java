package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import java.util.Set;

public class DiscountsViewController implements IController {

    private static final Set<PosEventType> eventTypesToListenFor = Set.of(
            PosEventType.DO_OPEN_DISCOUNTS
    );

    private final IPosEventDispatcher parentPosEventDispatcher;
    private final DiscountsView discountsView;

    public DiscountsViewController(@NonNull String frameTitle, @NonNull IPosEventDispatcher parentPosEventDispatcher) {
        this.parentPosEventDispatcher = parentPosEventDispatcher;
        discountsView = new DiscountsView(frameTitle, this);
    }

    DiscountsViewController(@NonNull IPosEventDispatcher parentPosEventDispatcher,
                            @NonNull DiscountsView discountsView) {
        this.parentPosEventDispatcher = parentPosEventDispatcher;
        this.discountsView = discountsView;
    }

    @Override
    public void onPosEvent(@NonNull PosEvent event) {
        switch (event.getType()) {
            case DO_OPEN_DISCOUNTS -> discountsView.setVisible(true);
        }
    }

    @Override
    public void dispatchPosEvent(PosEvent event) {
        parentPosEventDispatcher.dispatchPosEvent(event);
    }

    @Override
    public @NonNull Set<PosEventType> getEventTypesToListenFor() {
        return eventTypesToListenFor;
    }
}

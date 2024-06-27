package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.component.IComponent;
import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.display.dto.ItemDto;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import java.util.Set;

/**
 * Controller for the pole display view. This class is responsible for updating the pole display view based on
 * POS events.
 */
public class PoleDisplayViewController implements IPosEventListener {

    private static final Set<PosEventType> eventTypesToListenFor = Set.of(
            PosEventType.TRANSACTION_STARTED,
            PosEventType.DO_OPEN_POLE_DISPLAY,
            PosEventType.ITEM_ADDED,
            PosEventType.ITEM_REMOVED
    );

    private final PoleDisplayView poleDisplayView;

    /**
     * Constructor that accepts a frame title.
     *
     * @param frameTitle The title of the frame.
     */
    public PoleDisplayViewController(@NonNull String frameTitle) {
        poleDisplayView = new PoleDisplayView(frameTitle);
    }

    /**
     * Constructor that accepts a pole display view. Package-private for testing purposes.
     *
     * @param poleDisplayView The pole display view.
     */
    PoleDisplayViewController(@NonNull PoleDisplayView poleDisplayView) {
        this.poleDisplayView = poleDisplayView;
    }

    @Override
    public void onPosEvent(@NonNull PosEvent event) {
        switch (event.getType()) {
            case TRANSACTION_STARTED -> {
                poleDisplayView.clearItems();
                poleDisplayView.setVisible(true);
            }
            case DO_OPEN_POLE_DISPLAY -> poleDisplayView.setVisible(true);
            case ITEM_ADDED -> {
                ItemDto itemDto = event.getProperty(ConstKeys.ITEM_DTO, ItemDto.class);
                poleDisplayView.addItem(itemDto);
            }
            case ITEM_REMOVED -> {
                ItemDto itemDto = event.getProperty(ConstKeys.ITEM_DTO, ItemDto.class);
                poleDisplayView.removeItem(itemDto);
            }
        }
    }

    @Override
    public @NonNull Set<PosEventType> getEventTypesToListenFor() {
        return eventTypesToListenFor;
    }
}

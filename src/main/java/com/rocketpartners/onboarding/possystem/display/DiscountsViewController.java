package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.commons.model.Discount;
import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import java.util.Map;
import java.util.Set;

/**
 * Controller for the discounts view.
 */
public class DiscountsViewController implements IPosEventListener {

    private static final Set<PosEventType> eventTypesToListenFor = Set.of(PosEventType.DO_SHOW_DISCOUNTS);

    private final DiscountsView discountsView;

    /**
     * Create a new discounts view controller.
     *
     * @param frameTitle the title of the frame
     */
    public DiscountsViewController(@NonNull String frameTitle) {
        discountsView = new DiscountsView(frameTitle);
    }

    @Override
    public void onPosEvent(@NonNull PosEvent event) {
        if (event.getType() == PosEventType.DO_SHOW_DISCOUNTS) {
            if (Application.DEBUG) {
                System.out.println("[DiscountsViewController] Showing discounts view");
            }
            discountsView.setVisible(true);

            Map<String, Discount> discounts = (Map<String, Discount>) event.getProperty(ConstKeys.DISCOUNTS);
            if (Application.DEBUG) {
                System.out.println("[DiscountsViewController] Discounts: " + discounts);
            }
            if (discounts != null) {
                discountsView.setDiscounts(discounts);
            }
        }
    }

    @Override
    public @NonNull Set<PosEventType> getEventTypesToListenFor() {
        return eventTypesToListenFor;
    }
}

package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class DiscountsView extends JFrame {

    private static final int VIEW_WIDTH = 400;
    private static final int VIEW_HEIGHT = 500;

    private final IPosEventDispatcher parentPosDispatcher;

    public DiscountsView(@NonNull String frameTitle, @NonNull IPosEventDispatcher parentPosDispatcher) {
        super(frameTitle);
        this.parentPosDispatcher = parentPosDispatcher;

        setMinimumSize(new Dimension(VIEW_WIDTH, VIEW_HEIGHT));
    }
}

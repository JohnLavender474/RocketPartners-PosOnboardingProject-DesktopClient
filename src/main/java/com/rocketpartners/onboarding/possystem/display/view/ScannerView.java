package com.rocketpartners.onboarding.possystem.display.view;

import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.swing.*;

@RequiredArgsConstructor
public class ScannerView extends JFrame {

    @NonNull
    private final IPosEventDispatcher parentPosDispatcher;

    public void setActive() {

    }

    public void setInactive() {

    }
}

package com.rocketpartners.onboarding.possystem.display.controller;

import com.rocketpartners.onboarding.possystem.component.IComponent;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;

/**
 * Interface for all controllers in the POS system.
 */
public interface IController extends IPosEventDispatcher, IPosEventListener, IComponent {
}

package com.rocketpartners.onboarding.possystem.event;

public interface IPosEventManager {

    void dispatchPosEvent(PosEvent event);

    void registerPosEventListener(IPosEventListener listener);

    void unregisterPosEventListener(IPosEventListener listener);
}

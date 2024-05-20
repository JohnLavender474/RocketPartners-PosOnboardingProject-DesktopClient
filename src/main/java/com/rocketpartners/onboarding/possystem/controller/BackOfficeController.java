package com.rocketpartners.onboarding.possystem.controller;

import java.util.ArrayList;
import java.util.List;

public class BackOfficeController implements IController {

    private final List<PosController> posControllers;

    public BackOfficeController() {
        posControllers = new ArrayList<>();
    }

    public void addChildController(PosController posController) {
        posControllers.add(posController);
    }

    @Override
    public void bootUp() {
        posControllers.forEach(PosController::bootUp);
    }

    @Override
    public void update() {
        posControllers.forEach(PosController::update);
    }

    @Override
    public void shutdown() {
        posControllers.forEach(PosController::shutdown);
    }
}

package com.rocketpartners.onboarding.possystem;

import com.rocketpartners.onboarding.possystem.display.CustomerView;
import com.rocketpartners.onboarding.possystem.display.ScannerView;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import lombok.NonNull;

import javax.swing.*;

/**
 * Run this class to view the Swing GUI detached from the backend.
 */
public class TestSwingMainClass {

    /**
     * Mock implementation of the {@link IPosEventDispatcher} interface.
     */
    public static final class MockPosEventDispatcher implements IPosEventDispatcher {

        @Override
        public void dispatchPosEvent(@NonNull PosEvent posEvent) {
            System.out.println("Dispatching event: " + posEvent);
        }
    }

    /**
     * Main method to run the Swing GUI.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        testCustomerView();
        // testScannerView();
    }

    private static void testCustomerView() {
        SwingUtilities.invokeLater(() -> {
            CustomerView customerView = new CustomerView(new MockPosEventDispatcher(), "Test Store", 1);
            customerView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            customerView.setSize(800, 600);
            customerView.setVisible(true);
            customerView.showScanningInProgress();
        });
    }

    private static void testScannerView() {
        SwingUtilities.invokeLater(() -> {
            ScannerView scannerView = new ScannerView(new MockPosEventDispatcher());
            scannerView.setVisible(true);
            // scannerView.setActive();
            scannerView.setInactive();
        });
    }
}

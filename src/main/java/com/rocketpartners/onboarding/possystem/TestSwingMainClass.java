package com.rocketpartners.onboarding.possystem;

import com.rocketpartners.onboarding.possystem.display.view.CustomerView;

import javax.swing.*;

/**
 * Run this class to view the Swing GUI detached from the backend.
 */
public class TestSwingMainClass {

    /**
     * Main method to run the Swing GUI.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CustomerView customerView = new CustomerView();
            customerView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            customerView.setSize(800, 600);
            customerView.setVisible(true);
            customerView.showScanningInProgress();
        });
    }
}

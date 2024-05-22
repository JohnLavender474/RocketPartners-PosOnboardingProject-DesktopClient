package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

/**
 * View for the scanner. This class is responsible for displaying the scanner view and handling user input.
 */
public class ScannerView extends JFrame {

    private static final int MIN_WIDTH = 400;
    private static final int MIN_HEIGHT = 150;
    private static final String FRAME_TITLE = "Scanner View";
    private static final String ENTER_BUTTON_TEXT = "Enter";
    private static final String NOT_IN_PROGRESS_TEXT = "Scanning not currently in progress";

    private final IPosEventDispatcher parentPosDispatcher;
    private final JTextField scannerInput;
    private final JButton enterButton;
    private final JPanel scannerPanel;
    private final JPanel statusPanel;

    /**
     * Constructor that accepts a parent POS event dispatcher. The scanner view is created with a scanner input field
     * and
     * an enter button. The scanner view is set to not active by default.
     */
    public ScannerView(@NonNull IPosEventDispatcher parentPosDispatcher) {
        super(FRAME_TITLE);
        this.parentPosDispatcher = parentPosDispatcher;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(ScannerView.this,
                        "Are you sure you want to close this window?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setResizable(true);

        scannerInput = new JTextField(20);
        enterButton = new JButton(ENTER_BUTTON_TEXT);
        enterButton.addActionListener(e -> onEnterButtonClick());
        scannerPanel = new JPanel();
        scannerPanel.add(scannerInput);
        scannerPanel.add(enterButton);
        scannerPanel.setVisible(false);

        JLabel notInProgressLabel = new JLabel(NOT_IN_PROGRESS_TEXT);
        statusPanel = new JPanel();
        statusPanel.add(notInProgressLabel);

        setLayout(new CardLayout());
        add(scannerPanel, "ScannerPanel");
        add(statusPanel, "StatusPanel");

        setInactive();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Set the scanner view to active.
     */
    public void setActive() {
        setActive(true);
    }

    /**
     * Set the scanner view to inactive.
     */
    public void setInactive() {
        setActive(false);
    }

    private void setActive(boolean scanningActive) {
        scannerPanel.setVisible(scanningActive);
        statusPanel.setVisible(!scanningActive);
        enterButton.setVisible(scanningActive);
        pack();
    }

    /**
     * Handle the enter button click event. Dispatches a REQUEST_ADD_ITEM event to the parent dispatcher. The event
     * contains the item upc as a property. Package-private for testing.
     */
    void onEnterButtonClick() {
        parentPosDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_ADD_ITEM,
                Map.of(ConstKeys.ITEM_UPC, getScannerInput())));
        clearScannerInput();
    }

    /**
     * Get the scanner input.
     *
     * @return The scanner input.
     */
    public String getScannerInput() {
        return scannerInput.getText();
    }

    /**
     * Clear the scanner input.
     */
    public void clearScannerInput() {
        scannerInput.setText("");
    }
}

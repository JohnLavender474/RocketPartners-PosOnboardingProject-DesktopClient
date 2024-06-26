package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

/**
 * View for the scanner. This class is responsible for displaying the scanner view and handling user input.
 */
public class ScannerView extends JFrame implements KeyEventDispatcher {

    private static final int MIN_WIDTH = 600;
    private static final int MIN_HEIGHT = 200;
    private static final int SCANNER_INPUT_COLUMNS = 30;
    private static final String NOT_IN_PROGRESS_TEXT = "Scanning not currently in progress";
    private static final String PROMPT_TEXT = "Scan a barcode or input it manually. Press ENTER to submit.";

    private final IPosEventDispatcher parentPosDispatcher;
    private final JTextArea scannerInput;
    private final JTextArea promptArea;
    private final JPanel scannerPanel;

    private boolean scanningActive;

    /**
     * Constructor that accepts a parent POS event dispatcher. The scanner view is created with a scanner input field
     * and
     * an enter button. The scanner view is set to not active by default.
     */
    public ScannerView(@NonNull String frameTitle, @NonNull IPosEventDispatcher parentPosDispatcher) {
        super(frameTitle);
        this.parentPosDispatcher = parentPosDispatcher;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(ScannerView.this,
                        "Are you sure you want to hide this window? You can re-open it by clicking the " +
                                "'Open Scanner' button while scanning is in progress.",
                        "Hide Scanner View Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    setVisible(false);
                }
            }
        });
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setResizable(false);

        promptArea = new JTextArea(PROMPT_TEXT);
        promptArea.setEditable(false);

        scannerInput = new JTextArea();
        scannerInput.setColumns(SCANNER_INPUT_COLUMNS);
        scannerInput.setEditable(false);

        scannerPanel = new JPanel();
        scannerPanel.add(scannerInput);

        setLayout(new GridLayout(2, 1));
        add(promptArea);
        add(scannerPanel);

        setInactive();
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_TYPED) {
            if (!scanningActive) {
                return false;
            }

            requestUserFocus();

            char c = e.getKeyChar();

            if (c == KeyEvent.CHAR_UNDEFINED) {
                return false;
            }

            if (Character.isDigit(c) || Character.isAlphabetic(c)) {
                String input = scannerInput.getText();
                scannerInput.setText(input + c);
                return true;
            }

            if (c == KeyEvent.VK_BACK_SPACE) {
                String input = scannerInput.getText();
                if (!input.isEmpty()) {
                    scannerInput.setText(input.substring(0, input.length() - 1));
                }
                return true;
            }

            if (c == KeyEvent.VK_ENTER) {
                onEnter();
                return true;
            }
        }
        return false;
    }

    void onEnter() {
        parentPosDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_ADD_ITEM,
                Map.of(ConstKeys.ITEM_UPC, getScannerInput())));
        clearScannerInput();
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
        this.scanningActive = scanningActive;
        promptArea.setText(scanningActive ? PROMPT_TEXT : NOT_IN_PROGRESS_TEXT);
        scannerPanel.setEnabled(scanningActive);
        pack();
    }

    /**
     * Request user focus on the scanner input field.
     */
    public void requestUserFocus() {
        if (Application.DEBUG) {
            System.out.println("[ScannerView] Requesting user focus");
        }
        toFront();
        scannerInput.requestFocusInWindow();
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

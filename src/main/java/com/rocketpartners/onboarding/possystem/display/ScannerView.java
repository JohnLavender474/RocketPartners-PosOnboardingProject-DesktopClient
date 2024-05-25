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

    private static final int MIN_WIDTH = 675;
    private static final int MIN_HEIGHT = 200;
    private static final int TEXT_FIELD_COLUMNS = 30;
    private static final String ENTER_BUTTON_TEXT = "Enter";
    private static final String NOT_IN_PROGRESS_TEXT = "Scanning not currently in progress";
    private static final String PROMPT_TEXT = "Scan a barcode or input the barcode manually. Press Enter to submit.";

    private final IPosEventDispatcher parentPosDispatcher;
    private final JTextField scannerInput;
    private final JTextArea promptArea;
    private final JButton enterButton;
    private final JPanel scannerPanel;

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
                        "Are you sure you want to close this window?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setResizable(false);

        promptArea = new JTextArea(PROMPT_TEXT);
        promptArea.setEditable(false);

        scannerInput = new JTextField(TEXT_FIELD_COLUMNS);

        enterButton = new JButton(ENTER_BUTTON_TEXT);
        enterButton.addActionListener(e -> onEnterButtonClick());
        getRootPane().setDefaultButton(enterButton);

        scannerPanel = new JPanel();
        scannerPanel.add(scannerInput);
        scannerPanel.add(enterButton);

        setLayout(new GridLayout(2, 1));
        add(promptArea);
        add(scannerPanel);

        setInactive();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            if (c == KeyEvent.CHAR_UNDEFINED) {
                return false;
            }
            requestUserFocus();
            if (c == KeyEvent.VK_ENTER) {
                onEnterButtonClick();
                return true;
            }
        }
        return false;
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

    private void setActive(boolean scanningActive) {
        promptArea.setText(scanningActive ? PROMPT_TEXT : NOT_IN_PROGRESS_TEXT);
        scannerPanel.setEnabled(scanningActive);
        enterButton.setEnabled(scanningActive);
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

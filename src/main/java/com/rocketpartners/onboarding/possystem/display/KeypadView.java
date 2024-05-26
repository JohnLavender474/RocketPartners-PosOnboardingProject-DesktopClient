package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

/**
 * View for the keypad. This class is responsible for displaying the keypad and handling user input. The keypad is
 * used to enter the card number for payment.
 */
public class KeypadView extends JFrame {

    private static final int VIEW_WIDTH = 400;
    private static final int VIEW_HEIGHT = 500;
    private static final String BACK = "BACK";
    private static final String ENTER = "ENTER";
    private static final String ZERO = "0";
    private static final Font DISPLAY_AREA_FONT = new Font("Arial", Font.PLAIN, 24);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 18);
    private static final int GRID_ROWS = 4;
    private static final int GRID_COLUMNS = 3;
    private static final int MAX_DIGITS = 16;

    private final IPosEventDispatcher parentPosDispatcher;
    private final JTextArea displayArea;

    /**
     * Constructor that accepts a frame title and a parent POS event dispatcher. The keypad view is created with a
     * display area and buttons for the numbers 0-9, backspace, and enter.
     */
    public KeypadView(@NonNull String frameTitle, @NonNull IPosEventDispatcher parentPosDispatcher) {
        this.parentPosDispatcher = parentPosDispatcher;
        setTitle(frameTitle);
        setMinimumSize(new Dimension(VIEW_WIDTH, VIEW_HEIGHT));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(KeypadView.this,
                        "Closing this window will cause the card payment process to be canceled. Are you sure " +
                                "you want to continue?",
                        "Cancel Card Payment Process?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    setVisible(false);
                    parentPosDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_CANCEL_PAYMENT));
                }
            }
        });
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        displayArea = new JTextArea(1, 20);
        displayArea.setFont(DISPLAY_AREA_FONT);
        displayArea.setEditable(false);
        add(displayArea, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(GRID_ROWS, GRID_COLUMNS, 5, 5));

        for (int i = 1; i <= 9; i++) {
            addButton(buttonPanel, String.valueOf(i));
        }

        addButton(buttonPanel, BACK);
        addButton(buttonPanel, ZERO);
        addButton(buttonPanel, ENTER);

        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Clears the display area.
     */
    public void clearDisplayArea() {
        displayArea.setText("");
    }

    private void addButton(@NonNull JPanel panel, @NonNull String label) {
        JButton button = new JButton(label);
        button.setFont(BUTTON_FONT);
        button.addActionListener(new KeypadButtonClickListener());
        panel.add(button);
    }

    private class KeypadButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            String currentText = displayArea.getText();

            switch (command) {
                case BACK -> {
                    if (!currentText.isEmpty()) {
                        displayArea.setText(currentText.substring(0, currentText.length() - 1));
                    }
                }
                case ENTER -> {
                    if (currentText.length() < MAX_DIGITS) {
                        JOptionPane.showMessageDialog(KeypadView.this,
                                "Card number must be " + MAX_DIGITS + " digits" + ".");
                    }

                    if (currentText.length() == MAX_DIGITS &&
                            JOptionPane.showConfirmDialog(KeypadView.this,
                                    "Card number entered: " + currentText + ". Do you confirm to make" +
                                            " the full transaction payment with this card number?",
                                    "Confirm Payment with Card Number",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        SwingUtilities.invokeLater(() -> parentPosDispatcher.dispatchPosEvent(new PosEvent(
                                PosEventType.REQUEST_ENTER_CARD_NUMBER, Map.of(ConstKeys.CARD_NUMBER, currentText))));
                    }
                }
                default -> {
                    if (currentText.length() < MAX_DIGITS) {
                        displayArea.setText(currentText + command);
                    }
                }
            }
        }
    }
}
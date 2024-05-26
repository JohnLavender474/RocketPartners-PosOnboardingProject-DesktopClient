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
 * View for the card keypad. This class is responsible for displaying the keypad and handling user input. The keypad is
 * used to enter the card number for payment.
 */
public class PayWithCardView extends KeypadView {

    private static final int VIEW_WIDTH = 400;
    private static final int VIEW_HEIGHT = 500;
    private static final int MAX_DIGITS = 16;

    /**
     * Constructor that accepts a frame title and a parent POS event dispatcher. The keypad view is created with a
     * display area and buttons for the numbers 0-9, backspace, and enter.
     */
    public PayWithCardView(@NonNull String frameTitle, @NonNull IPosEventDispatcher parentPosDispatcher) {
        super();

        setTitle(frameTitle);
        setMinimumSize(new Dimension(VIEW_WIDTH, VIEW_HEIGHT));

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(PayWithCardView.this,
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

        addButtonActionListener(KeypadButton.BACK, e -> {
            String currentText = getDisplayAreaText();
            if (!currentText.isEmpty()) {
                setDisplayAreaText(currentText.substring(0, currentText.length() - 1));
            }
        });

        addButtonActionListener(KeypadButton.ENTER, e -> {
            String currentText = getDisplayAreaText();
            if (currentText.length() < MAX_DIGITS) {
                JOptionPane.showMessageDialog(PayWithCardView.this,
                        "Card number must be " + MAX_DIGITS + " digits" + ".");
            }

            if (currentText.length() == MAX_DIGITS &&
                    JOptionPane.showConfirmDialog(PayWithCardView.this,
                            "Card number entered: " + currentText + ". Do you confirm to make" +
                                    " the full transaction payment with this card number?",
                            "Confirm Payment with Card Number",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                SwingUtilities.invokeLater(() -> parentPosDispatcher.dispatchPosEvent(new PosEvent(
                        PosEventType.REQUEST_ENTER_CARD_NUMBER, Map.of(ConstKeys.CARD_NUMBER, currentText))));
            }
        });

        KeypadButton.getNumericButtons().forEach(it ->
                addButtonActionListener(it, e -> {
                    String currentText = getDisplayAreaText();
                    if (currentText.length() < MAX_DIGITS) {
                        setDisplayAreaText(currentText + it.getText());
                    }
                }));
    }
}
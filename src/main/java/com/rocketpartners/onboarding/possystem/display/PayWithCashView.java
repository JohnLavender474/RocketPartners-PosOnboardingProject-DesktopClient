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
 * View for the cash keypad. This class is responsible for displaying the keypad and handling user input. The keypad is
 * used to enter the cash amount for payment.
 */
public class PayWithCashView extends KeypadView {

    private static final int VIEW_WIDTH = 400;
    private static final int VIEW_HEIGHT = 500;
    private static final int MAX_CHARS = 8;

    /**
     * Constructor that accepts a frame title and a parent POS event dispatcher. The keypad view is created with a
     * display area and buttons for the numbers 0-9, backspace, and enter.
     */
    public PayWithCashView(@NonNull String frameTitle, @NonNull IPosEventDispatcher parentPosDispatcher) {
        super();

        setTitle(frameTitle);
        setMinimumSize(new Dimension(VIEW_WIDTH, VIEW_HEIGHT));

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(PayWithCashView.this,
                        "Closing this window will cause the cash payment process to be canceled. " +
                                "Are you sure you want to continue?",
                        "Cancel Cash Payment Process?",
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
            if (JOptionPane.showConfirmDialog(PayWithCashView.this,
                    "Cash amount entered: $" + currentText + ". Do you confirm to insert" +
                            " this amount of money?",
                    "Confirm Payment with Cash",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                clearDisplayAreaText();

                SwingUtilities.invokeLater(() -> parentPosDispatcher.dispatchPosEvent(new PosEvent(
                        PosEventType.REQUEST_INSERT_CASH, Map.of(ConstKeys.CASH_AMOUNT, currentText))));
            }
        });

        addButtonActionListener(KeypadButton.PERIOD, e -> {
            String currentText = getDisplayAreaText();
            if (currentText.contains(".")) {
                return;
            }
            setDisplayAreaText(currentText + ".");
        });

        KeypadButton.getNumericButtons().forEach(it ->
                addButtonActionListener(it, e -> {
                    String currentText = getDisplayAreaText();
                    String[] split = currentText.split("\\.");
                    if (split.length == 2 && split[1].length() == 2) {
                        return;
                    }
                    if (currentText.length() < MAX_CHARS) {
                        setDisplayAreaText(currentText + it.getText());
                    }
                }));
    }

    /**
     * Notifies the user that the cash amount entered is invalid and that the customer is a cheap dummy.
     */
    public void notifyInsufficientFunds(@NonNull String amountNeeded) {
        JOptionPane.showMessageDialog(PayWithCashView.this,
                "Insufficient funds! Still need to pay " + amountNeeded + ". Please gimme more cash, " +
                        "you dumb cheapskate.");
    }
}
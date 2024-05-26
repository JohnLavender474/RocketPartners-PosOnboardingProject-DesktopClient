package com.rocketpartners.onboarding.possystem.display;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;

public class KeypadView extends JFrame {

    /**
     * Enumeration of keypad buttons.
     */
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum KeypadButton {
        BACK("BACK"),
        ENTER("ENTER"),
        PERIOD("."),
        ZERO("0"),
        ONE("1"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9");

        private final String text;

        /**
         * Returns a collection of all numeric keypad buttons.
         *
         * @return A collection of all numeric keypad buttons.
         */
        public static Collection<KeypadButton> getNumericButtons() {
            return List.of(ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, ZERO);
        }
    }

    private static final Font DISPLAY_AREA_FONT = new Font("Arial", Font.PLAIN, 24);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 18);
    private static final int MAIN_GRID_ROWS = 2;
    private static final int MAIN_GRID_COLUMNS = 1;
    private static final int TOP_ROW_GRID_COLUMNS = 3;
    private static final int NUMERIC_GRID_ROWS = 3;
    private static final int NUMERIC_GRID_COLUMNS = 4;

    private final JTextArea displayArea;
    private final Map<KeypadButton, JButton> keypadButtons;

    /**
     * Constructor that accepts a frame title and a parent POS event dispatcher. The keypad view is created with a
     * display area and buttons for the numbers 0-9, backspace, and enter.
     */
    public KeypadView() {
        keypadButtons = new EnumMap<>(KeypadButton.class);

        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        displayArea = new JTextArea(1, 20);
        displayArea.setFont(DISPLAY_AREA_FONT);
        displayArea.setEditable(false);

        add(displayArea, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(MAIN_GRID_ROWS, MAIN_GRID_COLUMNS, 5, 5));

        JPanel topRowPanel = new JPanel(new GridLayout(1, TOP_ROW_GRID_COLUMNS));
        List<KeypadButton> topRowButtons = List.of(KeypadButton.BACK, KeypadButton.ENTER, KeypadButton.PERIOD);
        topRowButtons.forEach(it -> {
            JButton button = createButton(it);
            topRowPanel.add(button);
            keypadButtons.put(it, button);
        });
        buttonPanel.add(topRowPanel);

        JPanel numbersPanel = new JPanel(new GridLayout(NUMERIC_GRID_ROWS, NUMERIC_GRID_COLUMNS, 5, 5));
        KeypadButton.getNumericButtons().forEach(it -> {
            JButton button = createButton(it);
            numbersPanel.add(button);
            keypadButtons.put(it, button);
        });
        buttonPanel.add(numbersPanel);

        add(buttonPanel);
    }

    private JButton createButton(@NonNull KeypadButton keypadButton) {
        JButton button = new JButton(keypadButton.getText());
        button.setFont(BUTTON_FONT);
        return button;
    }

    /**
     * Clears the display area.
     */
    public void clearDisplayAreaText() {
        displayArea.setText("");
    }

    /**
     * Sets the text of the display area.
     *
     * @param text The text to set.
     */
    public void setDisplayAreaText(@NonNull String text) {
        displayArea.setText(text);
    }

    /**
     * Returns the text of the display area.
     *
     * @return The text of the display area.
     */
    public String getDisplayAreaText() {
        return displayArea.getText();
    }

    /**
     * Adds an action listener to a keypad button.
     *
     * @param keypadButton   The keypad button.
     * @param actionListener The action listener.
     */
    public void addButtonActionListener(@NonNull KeypadButton keypadButton, @NonNull ActionListener actionListener) {
        JButton button = keypadButtons.get(keypadButton);
        button.addActionListener(actionListener);
    }
}

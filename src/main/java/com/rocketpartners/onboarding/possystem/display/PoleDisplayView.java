package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.display.dto.ItemDto;
import com.rocketpartners.onboarding.possystem.utils.Pair;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.LinkedList;

/**
 * View for the pole display. This class is responsible for displaying the pole display and handling the display of
 * items as they are added and removed from the transaction.
 */
public class PoleDisplayView extends JFrame {

    private static final int DISPLAY_WIDTH = 500;
    private static final int DISPLAY_HEIGHT = 300;
    private static final int MAX_ITEMS = 8;
    private static final int MAX_ITEM_NAME_LENGTH = 15;
    private static final int ROW_LENGTH = 25;
    private static final String ADDED = "ADDED";
    private static final String REMOVED = "REMOVED";
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color ADDED_COLOR = Color.GREEN;
    private static final Color REMOVED_COLOR = Color.RED;
    private static final Font TEXT_FONT = new Font("Monospaced", Font.PLAIN, 24);

    private final LinkedList<Pair<String, String>> messages;
    private final JPanel displayPanel;

    /**
     * Constructor that accepts a frame title.
     *
     * @param frameTitle The title of the frame.
     */
    public PoleDisplayView(@NonNull String frameTitle) {
        super(frameTitle);
        setMinimumSize(new Dimension(DISPLAY_WIDTH, DISPLAY_HEIGHT));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(PoleDisplayView.this,
                        "Close the pole display view? You can reopen it from the POS system while scanning is" +
                                "in progress.",
                        "Close Pole Display View?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    setVisible(false);
                }
            }
        });
        setBackground(BACKGROUND_COLOR);
        setResizable(false);

        messages = new LinkedList<>();

        displayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMessages(g);
            }
        };
        displayPanel.setBackground(BACKGROUND_COLOR);
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        add(displayPanel);
    }

    /**
     * Adds an item to the pole display.
     *
     * @param itemDto The item to add.
     */
    public void addItem(@NonNull ItemDto itemDto) {
        if (messages.size() >= MAX_ITEMS) {
            messages.poll();
        }
        String message = formatMessage(itemDto.getName(), itemDto.getUnitPrice());
        messages.offer(Pair.of(ADDED, message));
        displayPanel.repaint();
    }

    /**
     * Removes an item from the pole display.
     *
     * @param itemDto The item to remove.
     */
    public void removeItem(@NonNull ItemDto itemDto) {
        if (messages.size() >= MAX_ITEMS) {
            messages.poll();
        }
        String message = formatMessage(itemDto.getName(), itemDto.getUnitPrice());
        messages.offer(Pair.of(REMOVED, message));
        displayPanel.repaint();
    }

    /**
     * Clears all items from the pole display.
     */
    public void clearItems() {
        messages.clear();
        displayPanel.repaint();
    }

    private void drawMessages(Graphics g) {
        g.setFont(TEXT_FONT);
        int y = 30;
        for (Pair<String, String> message : messages) {
            String type = message.getKey();
            Color color = type.equals(ADDED) ? ADDED_COLOR : REMOVED_COLOR;
            g.setColor(color);
            g.drawString(message.getValue(), 10, y);
            y += 30;
        }
    }

    private String formatMessage(@NonNull String itemName, @NonNull BigDecimal price) {
        if (itemName.length() > MAX_ITEM_NAME_LENGTH) {
            itemName = itemName.substring(0, MAX_ITEM_NAME_LENGTH - 3) + "...";
        }
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        String priceStr = currencyFormat.format(price);
        int padding = ROW_LENGTH - itemName.length() - priceStr.length();
        return itemName + " ".repeat(Math.max(0, padding)) + priceStr;
    }
}

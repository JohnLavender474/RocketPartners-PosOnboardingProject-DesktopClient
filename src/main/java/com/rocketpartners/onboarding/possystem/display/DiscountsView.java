package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.commons.model.Discount;
import com.rocketpartners.onboarding.possystem.Application;
import lombok.NonNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * View for displaying discounts.
 */
public class DiscountsView extends JFrame {

    private static final int VIEW_WIDTH = 400;
    private static final int VIEW_HEIGHT = 500;

    /**
     * Table model for displaying discounts.
     */
    static final class DiscountsTableModel extends DefaultTableModel {

        private static final String[] COLUMN_NAMES = {"Item UPC", "Type", "Value"};

        /**
         * Create a new discounts table model.
         */
        public DiscountsTableModel() {
            super(COLUMN_NAMES, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * Set the discounts to display.
         *
         * @param discounts the discounts to display
         */
        public void setDiscounts(@NonNull Map<String, Discount> discounts) {
            setRowCount(0);
            discounts.forEach((key, value) -> {
                List<Object> row = new ArrayList<>();
                row.add(key);
                row.add(value.getType());
                row.add(value.getValue());
                addRow(row.toArray());
            });
        }
    }

    private final DiscountsTableModel discountsTableModel;

    /**
     * Create a new discounts view.
     *
     * @param frameTitle the title of the frame
     */
    public DiscountsView(@NonNull String frameTitle) {
        super(frameTitle);
        setMinimumSize(new Dimension(VIEW_WIDTH, VIEW_HEIGHT));

        discountsTableModel = new DiscountsTableModel();
        JTable table = new JTable(discountsTableModel);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Set the discounts to display.
     *
     * @param discounts the discounts to display
     */
    public void setDiscounts(@NonNull Map<String, Discount> discounts) {
        discountsTableModel.setDiscounts(discounts);
        revalidate();
        repaint();
        if (Application.DEBUG) {
            System.out.println("[DiscountsView] Setting discounts: " + discounts);
        }
    }
}

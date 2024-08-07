package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.constant.ConstVals;
import com.rocketpartners.onboarding.commons.model.ItemDto;
import com.rocketpartners.onboarding.commons.model.LineItemDto;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The customer view of the POS system.
 */
public class CustomerView extends JFrame {

    /**
     * A table model that represents the transactions table. This table model is used to display the transactions table
     * in the customer view. The first column is a checkbox column that allows the user to select line items to void.
     * The second column is the UPC of the item. The third column is the name of the item. The fourth column is the unit
     * price of the item. The fifth column is the quantity of the item. The sixth column is the total price of the item.
     * The table model is used to update the transactions table with line item DTOs.
     */
    final class TransactionTableModel extends DefaultTableModel {

        /**
         * Constructor that initializes the transactions table model.
         */
        public TransactionTableModel() {
            super(new String[]{
                    TRANSACTION_TABLE_COLUMN_CHECKBOX,
                    TRANSACTION_TABLE_COLUMN_STATUS,
                    TRANSACTION_TABLE_COLUMN_UPC,
                    TRANSACTION_TABLE_COLUMN_ITEM_NAME,
                    TRANSACTION_TABLE_COLUMN_PRICE,
                    TRANSACTION_TABLE_COLUMN_QUANTITY,
                    TRANSACTION_TABLE_COLUMN_TOTAL
            }, 0);
        }

        /**
         * Returns if the line item at the specified row is voided.
         *
         * @param row The row index.
         * @return True if the line item is voided, false otherwise.
         */
        public boolean isVoided(int row) {
            if (row < 0 || row >= getRowCount()) {
                return false;
            }
            return STATUS_VOIDED.equals(getValueAt(row, 1));
        }

        public boolean isSelected(int row) {
            if (row < 0 || row >= getRowCount()) {
                return false;
            }
            return (Boolean) getValueAt(row, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            if (!STATUS_ADDED.equals(getValueAt(row, 1))) {
                return false;
            }
            return column == 0 || column == 5;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            }
            return String.class;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            super.setValueAt(aValue, row, column);
            if (aValue instanceof Boolean checked && column == 0) {
                String upc = (String) getValueAt(row, 2);
                if (upc == null) {
                    // TODO: Every time the table is updated, this method is called when the values are null.
                    //  Therefore, this error will trigger (sometimes multiple times) every time the table is updated.
                    System.err.println("[CustomerView] UPC is null at row " + row + ", column " + column);
                    return;
                }
                if (checked) {
                    selectedLineItemUpcs.add(upc);
                } else {
                    selectedLineItemUpcs.remove(upc);
                }
                updateVoidButtonText();
                repaint();
            }
        }
    }

    /**
     * A table cell renderer that renders the status column of the transactions table. This renderer is used to render
     * the status column of the transactions table in the customer view. The status column displays the status of the
     * line item. If the status is "ADDED", the checkbox in the first column is enabled. If the status is "VOIDED", the
     * checkbox in the first column is disabled. The renderer is used to update the transactions table with line item
     * DTOs. The renderer updates the transactions table with line item DTOs.
     */
    static final class SelectCellRenderer extends JCheckBox implements TableCellRenderer {

        private final TransactionTableModel model;

        public SelectCellRenderer(@NonNull TransactionTableModel model) {
            this.model = model;
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            if (value instanceof Boolean) {
                this.setSelected((Boolean) value);
                String status = (String) table.getModel().getValueAt(row, 1);
                this.setEnabled(STATUS_ADDED.equals(status));
            }
            setRowColors(this, model, row);
            return this;
        }
    }

    /**
     * A table cell renderer that renders the quantity column of the transactions table. This renderer is used to render
     * the quantity column of the transactions table in the customer view. The quantity column displays the quantity of
     * the line item. The renderer is used to update the transactions table with line item DTOs. The renderer updates
     * the transactions table with line item DTOs.
     */
    static final class QuantityCellRenderer extends JPanel implements TableCellRenderer {

        private final TransactionTableModel model;
        private final JButton decrementButton;
        private final JTextField quantityField;

        /**
         * Constructor that initializes the quantity cell renderer.
         */
        public QuantityCellRenderer(@NonNull TransactionTableModel model) {
            this.model = model;
            setLayout(new BorderLayout());

            //noinspection DuplicatedCode
            decrementButton = new JButton("-");
            quantityField = new JTextField("1", 3);
            JButton incrementButton = new JButton("+");

            quantityField.setPreferredSize(new Dimension(QUANTITY_FIELD_WIDTH, QUANTITY_FIELD_HEIGHT));
            quantityField.setHorizontalAlignment(SwingConstants.CENTER);
            quantityField.setEditable(false);

            Dimension buttonSize = new Dimension(INCREMENT_DECREMENT_BUTTON_WIDTH, INCREMENT_DECREMENT_BUTTON_HEIGHT);
            decrementButton.setPreferredSize(buttonSize);
            incrementButton.setPreferredSize(buttonSize);
            decrementButton.setFont(decrementButton.getFont().deriveFont(INCREMENT_DECREMENT_BUTTON_FONT_SIZE));
            incrementButton.setFont(incrementButton.getFont().deriveFont(INCREMENT_DECREMENT_BUTTON_FONT_SIZE));

            decrementButton.setEnabled(false);

            add(decrementButton, BorderLayout.WEST);
            add(quantityField, BorderLayout.CENTER);
            add(incrementButton, BorderLayout.EAST);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            if (value instanceof Integer) {
                quantityField.setText(value.toString());
                decrementButton.setEnabled((Integer) value > 1);
            }
            setRowColors(this, model, row);
            return this;
        }
    }

    /**
     * A table cell editor that edits the quantity column of the transactions table. This editor is used to edit the
     * quantity column of the transactions table in the customer view. The quantity column displays the quantity of the
     * line item. The editor is used to update the transactions table with line item DTOs. The editor updates the
     * transactions table with line item DTOs.
     */
    final class QuantityCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel;
        private final JButton decrementButton;
        private final JTextField quantityField;

        /**
         * Constructor that initializes the quantity cell editor.
         */
        public QuantityCellEditor() {
            panel = new JPanel(new BorderLayout());

            //noinspection DuplicatedCode
            decrementButton = new JButton("-");
            quantityField = new JTextField("1", 3);
            JButton incrementButton = new JButton("+");

            quantityField.setPreferredSize(new Dimension(QUANTITY_FIELD_WIDTH, QUANTITY_FIELD_HEIGHT));
            quantityField.setHorizontalAlignment(SwingConstants.CENTER);
            quantityField.setEditable(false);

            Dimension buttonSize = new Dimension(INCREMENT_DECREMENT_BUTTON_WIDTH, INCREMENT_DECREMENT_BUTTON_HEIGHT);
            decrementButton.setPreferredSize(buttonSize);
            incrementButton.setPreferredSize(buttonSize);
            decrementButton.setFont(decrementButton.getFont().deriveFont(INCREMENT_DECREMENT_BUTTON_FONT_SIZE));
            incrementButton.setFont(incrementButton.getFont().deriveFont(INCREMENT_DECREMENT_BUTTON_FONT_SIZE));

            panel.add(decrementButton, BorderLayout.WEST);
            panel.add(quantityField, BorderLayout.CENTER);
            panel.add(incrementButton, BorderLayout.EAST);

            decrementButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                String upc = (String) transactionTable.getModel().getValueAt(transactionTable.getEditingRow(), 2);
                if (Application.DEBUG) {
                    System.out.println("[CustomerView] Decrementing quantity of line item: " + upc);
                }
                int value = Integer.parseInt(quantityField.getText());
                if (value > 1) {
                    value--;
                    if (Application.DEBUG) {
                        System.out.println("[CustomerView] Quantity decremented to: " + value);
                    }
                    quantityField.setText(String.valueOf(value));
                    decrementButton.setEnabled(value > 1);
                    parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_REMOVE_ITEM,
                            Map.of(ConstKeys.ITEM_UPC, upc)));
                } else if (Application.DEBUG) {
                    System.out.println("[CustomerView] Quantity cannot be less than 1");
                }

                stopCellEditing();
            }));

            incrementButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                String upc = (String) transactionTable.getModel().getValueAt(transactionTable.getEditingRow(), 2);
                if (Application.DEBUG) {
                    System.out.println("[CustomerView] Incrementing quantity of line item: " + upc);
                }
                int value = Integer.parseInt(quantityField.getText());
                value++;
                if (Application.DEBUG) {
                    System.out.println("[CustomerView] Quantity incremented to: " + value);
                }
                quantityField.setText(String.valueOf(value));
                decrementButton.setEnabled(value > 1);
                parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_ADD_ITEM,
                        Map.of(ConstKeys.ITEM_UPC, upc)));

                stopCellEditing();
            }));
        }

        @Override
        public Object getCellEditorValue() {
            return Integer.parseInt(quantityField.getText());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                                                     int column) {
            if (value instanceof Integer i) {
                quantityField.setText(String.valueOf(i));
            }
            return panel;
        }
    }

    /**
     * A table cell renderer that renders the status column of the transactions table. This renderer is used to render
     * the status column of the transactions table in the customer view. The status column displays the status of the
     * line item. If the status is "ADDED", the checkbox in the first column is enabled. If the status is "VOIDED", the
     * checkbox in the first column is disabled. The renderer is used to update the transactions table with line item
     * DTOs. The renderer updates the transactions table with line item DTOs.
     */
    final class StandardCellRenderer extends DefaultTableCellRenderer {

        private final TransactionTableModel model;

        /**
         * Constructor that initializes the standard cell renderer.
         *
         * @param model The transaction table model.
         */
        public StandardCellRenderer(@NonNull TransactionTableModel model) {
            this.model = model;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setRowColors(component, model, row);
            return component;
        }
    }

    private static void setRowColors(@NonNull Component component, @NonNull TransactionTableModel model, int row) {
        if (model.isVoided(row)) {
            component.setForeground(Color.RED);
            component.setBackground(Color.DARK_GRAY);
        } else if (model.isSelected(row)) {
            component.setForeground(Color.BLACK);
            component.setBackground(Color.WHITE);
        } else {
            component.setForeground(Color.WHITE);
            component.setBackground(Color.GRAY);
        }
    }

    private static final int CUSTOMER_VIEW_FRAME_WIDTH = 1200;
    private static final int CUSTOMER_VIEW_FRAME_HEIGHT = 750;

    private static final int STANDARD_BUTTON_WIDTH = 35;
    private static final int STANDARD_BUTTON_HEIGHT = 65;
    private static final Font STANDARD_BUTTON_FONT = new Font("Courier New", Font.BOLD, 18);

    private static final int QUANTITY_FIELD_WIDTH = 30;
    private static final int QUANTITY_FIELD_HEIGHT = 20;
    private static final int INCREMENT_DECREMENT_BUTTON_WIDTH = 20;
    private static final int INCREMENT_DECREMENT_BUTTON_HEIGHT = 20;
    private static final float INCREMENT_DECREMENT_BUTTON_FONT_SIZE = 8.0f;

    private static final String WELCOME_BANNER_TEXT = "WELCOME TO %s";
    private static final String NO_TRANSACTION_IN_PROGRESS_CONTENT_EXT = "PLEASE CLICK THE BUTTON TO START A " +
            "TRANSACTION";
    private static final String TRANSACTION_IN_PROGRESS_BANNER_TEXT = "TRANSACTION IN PROGRESS. SCAN YOUR ITEMS NOW.";
    private static final String TRANSACTION_VOIDED_BANNER_TEXT = "TRANSACTION VOIDED";
    private static final String TRANSACTION_ENDED_TEXT = "TRANSACTION ENDED";
    private static final String TRANSACTION_COMPLETED_BANNER_TEXT = "TRANSACTION COMPLETED";
    private static final String AWAITING_PAYMENT_BANNER_TEXT = "AWAITING PAYMENT";
    private static final String TRANSACTION_COMPLETED_MESSAGE = "We appreciate your business! Your receipt is now " +
            "displayed on the receipt display";

    private static final String TRANSACTION_TABLE_TITLE = "Transaction";
    private static final String TRANSACTION_TABLE_COLUMN_CHECKBOX = "Select";
    private static final String TRANSACTION_TABLE_COLUMN_STATUS = "Status";
    private static final String TRANSACTION_TABLE_COLUMN_UPC = "UPC";
    private static final String TRANSACTION_TABLE_COLUMN_ITEM_NAME = "Item Name";
    private static final String TRANSACTION_TABLE_COLUMN_PRICE = "Unit Price";
    private static final String TRANSACTION_TABLE_COLUMN_QUANTITY = "Quantity";
    private static final String TRANSACTION_TABLE_COLUMN_TOTAL = "Total";
    private static final float[] TRANSACTION_TABLE_COLUMN_WIDTHS = {10f, 10f, 20f, 30f, 10f, 10f, 10f};

    private static final int QUICK_ITEMS_ROWS_COUNT = 2;
    private static final int QUICK_ITEMS_COLUMNS_COUNT = 4;
    private static final int QUICK_ITEM_TEXT_MAX_LENGTH = 25;

    private static final String STATUS_ADDED = "ADDED";
    private static final String STATUS_VOIDED = "VOIDED";

    private static final String START_TRANSACTION_BUTTON_TEXT = "Start Transaction";
    private static final String OPEN_SCANNER_BUTTON_TEXT = "Open Scanner";
    private static final String OPEN_POLE_DISPLAY_BUTTON_TEXT = "Open Pole Display";
    private static final String PAY_WITH_CARD_BUTTON_TEXT = "Pay with Card";
    private static final String PAY_WITH_CASH_BUTTON_TEXT = "Pay with Cash";
    private static final String OPEN_DISCOUNTS_BUTTON_TEXT = "Open Discounts";
    private static final String VOID_TRANSACTION_BUTTON_TEXT = "Void Transaction";
    private static final String VOID_LINE_ITEMS_BUTTON_TEXT = "Void Line Items";
    private static final String CANCEL_PAYMENT_BUTTON_TEXT = "Cancel Payment";
    private static final String CONTINUE_BUTTON_TEXT = "Continue";

    private final IPosEventDispatcher parentEventDispatcher;
    private final Set<String> selectedLineItemUpcs;
    private final CircularFifoQueue<ItemDto> quickItemDtos;

    private JLabel bannerLabel;

    private JPanel contentPanel;
    private JPanel quickItemsPanel;

    private JTextArea storeNameTextArea;
    private JTextArea posInfoTextArea;
    private JTextArea transactionNumberTextArea;

    private JTextArea subtotalTextArea;
    private JTextArea discountsTextArea;
    private JTextArea taxesTextArea;
    private JTextArea totalTextArea;
    private JTextArea amountTenderedTextArea;
    private JTextArea changeDueTextArea;

    private JTable transactionTable;

    private JButton startTransactionButton;
    private JButton payWithCardButton;
    private JButton payWithCashButton;
    private JButton openDiscountsButton;
    private JButton openScannerButton;
    private JButton openPoleDisplayButton;
    private JButton voidButton;
    private JButton cancelPaymentButton;
    private JButton continueButton;

    private final String storeName;

    /**
     * Constructor that accepts a parent event dispatcher, store name, and POS lane number.
     *
     * @param parentEventDispatcher The parent event dispatcher.
     * @param storeName             The name of the store.
     * @param posLane               The POS lane number.
     */
    public CustomerView(@NonNull IPosEventDispatcher parentEventDispatcher,
                        @NonNull String storeName, int posLane) {
        super("Customer View - Lane " + posLane);

        if (Application.DEBUG) {
            System.out.println("[CustomerView] Creating Customer View for Store Name: " + storeName + " and Lane: " + posLane);
        }

        this.storeName = storeName;
        this.parentEventDispatcher = parentEventDispatcher;

        setMinimumSize(new Dimension(CUSTOMER_VIEW_FRAME_WIDTH, CUSTOMER_VIEW_FRAME_HEIGHT));
        setPreferredSize(new Dimension(CUSTOMER_VIEW_FRAME_WIDTH, CUSTOMER_VIEW_FRAME_HEIGHT));
        setResizable(true);

        initializeComponents(storeName, posLane);

        setLayout(new BorderLayout());
        add(bannerLabel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(CustomerView.this,
                        "Closing this window will shutdown the application. Are you sure you want to do this?",
                        "Shut Down the Application?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        selectedLineItemUpcs = new HashSet<>();
        quickItemDtos = new CircularFifoQueue<>(ConstVals.QUICK_ITEMS_COUNT);
    }

    private void initializeComponents(@NonNull String storeName, int posLane) {
        bannerLabel = new JLabel("LOADING! PLEASE WAIT.", SwingConstants.CENTER);

        contentPanel = new JPanel();
        quickItemsPanel = new JPanel(new GridLayout(QUICK_ITEMS_ROWS_COUNT, QUICK_ITEMS_COLUMNS_COUNT));

        TransactionTableModel model = new TransactionTableModel();
        transactionTable = new JTable(model);
        transactionTable.setRowSelectionAllowed(false);
        transactionTable.setColumnSelectionAllowed(false);
        transactionTable.setCellSelectionEnabled(false);
        transactionTable.setBackground(Color.LIGHT_GRAY);
        transactionTable.setSelectionBackground(Color.WHITE);
        TableColumnModel columnModel = transactionTable.getColumnModel();
        columnModel.getColumn(0).setCellRenderer(new SelectCellRenderer(model));
        columnModel.getColumn(1).setCellRenderer(new StandardCellRenderer(model));
        columnModel.getColumn(2).setCellRenderer(new StandardCellRenderer(model));
        columnModel.getColumn(3).setCellRenderer(new StandardCellRenderer(model));
        columnModel.getColumn(4).setCellRenderer(new StandardCellRenderer(model));
        columnModel.getColumn(5).setCellRenderer(new QuantityCellRenderer(model));
        columnModel.getColumn(5).setCellEditor(new QuantityCellEditor());
        columnModel.getColumn(6).setCellRenderer(new StandardCellRenderer(model));
        transactionTable.getTableHeader().setReorderingAllowed(false);
        transactionTable.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                resetTransactionsTableColumnWidths();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                resetTransactionsTableColumnWidths();
            }
        });

        storeNameTextArea = new JTextArea();
        storeNameTextArea.setEditable(false);
        storeNameTextArea.setText(storeName);

        posInfoTextArea = new JTextArea();
        posInfoTextArea.setEditable(false);
        posInfoTextArea.setText("POS Lane: " + posLane);

        transactionNumberTextArea = new JTextArea();
        transactionNumberTextArea.setEditable(false);
        transactionNumberTextArea.setText("Transaction Number: N/A");

        subtotalTextArea = new JTextArea();
        subtotalTextArea.setEditable(false);

        discountsTextArea = new JTextArea();
        discountsTextArea.setEditable(false);

        taxesTextArea = new JTextArea();
        taxesTextArea.setEditable(false);

        totalTextArea = new JTextArea();
        totalTextArea.setEditable(false);

        amountTenderedTextArea = new JTextArea();
        amountTenderedTextArea.setEditable(false);

        changeDueTextArea = new JTextArea();
        changeDueTextArea.setEditable(false);

        startTransactionButton = createButton(START_TRANSACTION_BUTTON_TEXT, Color.getHSBColor(200f / 360f, 0.9f,
                0.85f));
        startTransactionButton.addActionListener(e ->
                SwingUtilities.invokeLater(() ->
                        parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_START_TRANSACTION))
                )
        );

        payWithCardButton = createButton(PAY_WITH_CARD_BUTTON_TEXT, Color.getHSBColor(140f / 360f, 0.8f, 0.4f));
        payWithCardButton.addActionListener(e ->
                SwingUtilities.invokeLater(() ->
                        parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_START_PAY_WITH_CARD_PROCESS))
                )
        );

        payWithCashButton = createButton(PAY_WITH_CASH_BUTTON_TEXT, Color.getHSBColor(140f / 360f, 0.8f, 0.4f));
        payWithCashButton.addActionListener(e ->
                SwingUtilities.invokeLater(() ->
                        parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_START_PAY_WITH_CASH_PROCESS))
                )
        );

        openDiscountsButton = createButton(OPEN_DISCOUNTS_BUTTON_TEXT, Color.getHSBColor(300f / 360f, 1f, 0.5f));
        openDiscountsButton.addActionListener(e ->
                SwingUtilities.invokeLater(() ->
                        parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_SHOW_DISCOUNTS))
                )
        );

        openScannerButton = createButton(OPEN_SCANNER_BUTTON_TEXT, Color.getHSBColor(200f / 360f, 0.9f, 0.85f));
        openScannerButton.addActionListener(e ->
                SwingUtilities.invokeLater(() ->
                        parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_OPEN_SCANNER))
                )
        );

        openPoleDisplayButton = createButton(OPEN_POLE_DISPLAY_BUTTON_TEXT,
                Color.getHSBColor(200f / 360f, 0.9f, 0.85f));
        openPoleDisplayButton.addActionListener(e ->
                SwingUtilities.invokeLater(() ->
                        parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_OPEN_POLE_DISPLAY))
                )
        );

        voidButton = createButton(VOID_TRANSACTION_BUTTON_TEXT, Color.getHSBColor(7f / 360f, 0.9f, 0.8f));
        voidButton.addActionListener(e -> SwingUtilities.invokeLater(this::requestVoidButtonAction));

        cancelPaymentButton = createButton(CANCEL_PAYMENT_BUTTON_TEXT, Color.getHSBColor(7f / 360f, 0.9f, 0.8f));
        cancelPaymentButton.addActionListener(e ->
                SwingUtilities.invokeLater(() ->
                        parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_CANCEL_PAYMENT))
                )
        );

        continueButton = createButton(CONTINUE_BUTTON_TEXT, Color.getHSBColor(200f / 360f, 0.9f, 0.85f));
        continueButton.addActionListener(e ->
                SwingUtilities.invokeLater(() ->
                        parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_RESET_POS))
                )
        );
    }

    private JButton createButton(@NonNull String text, @NonNull Color backgroundColor) {
        JButton button = new JButton(text);
        button.setSize(STANDARD_BUTTON_WIDTH, STANDARD_BUTTON_HEIGHT);
        button.setFont(STANDARD_BUTTON_FONT);
        button.setOpaque(true);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        return button;
    }

    /**
     * Reset the view.
     */
    public void reset() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Resetting customer view");
        }
        selectedLineItemUpcs.clear();
        quickItemDtos.clear();
        clearTransactionTableRows();
        resetTransactionsTableColumnWidths();
        resetTransactionMetadata();
        revalidate();
        repaint();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        resetTransactionsTableColumnWidths();
        revalidate();
        repaint();
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Setting visibility to: " + visible);
        }
    }

    /**
     * Update the transactions table with the provided line item DTOs.
     *
     * @param lineItemDtos The line item DTOs.
     */
    public void updateTransactionsTable(@NonNull List<LineItemDto> lineItemDtos) {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Updating transactions table with line items DTOs: " + lineItemDtos);
        }

        clearTransactionTableRows();

        Set<String> upcsPriorUpdate = new HashSet<>(selectedLineItemUpcs);
        selectedLineItemUpcs.clear();

        lineItemDtos.forEach(it -> {
            addTransactionTableRow();
            int row = transactionTable.getRowCount() - 1;

            String upc = it.getItemUpc();
            boolean isSelected = upcsPriorUpdate.contains(upc);
            if (!it.isVoided() && isSelected) {
                selectedLineItemUpcs.add(upc);
            }

            boolean checkBoxValue = !it.isVoided() && isSelected;
            transactionTable.setValueAt(checkBoxValue, row, 0);

            String status = it.isVoided() ? STATUS_VOIDED : STATUS_ADDED;
            transactionTable.setValueAt(status, row, 1);

            transactionTable.setValueAt(upc, row, 2);
            transactionTable.setValueAt(it.getItemName(), row, 3);
            transactionTable.setValueAt(it.getUnitPrice(), row, 4);
            transactionTable.setValueAt(it.getQuantity(), row, 5);

            BigDecimal total = it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity()));
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            String totalFormatted = currencyFormat.format(total);
            transactionTable.setValueAt(totalFormatted, row, 6);
        });
    }

    /**
     * Reset the widths of the columns in the transactions table.
     */
    public void resetTransactionsTableColumnWidths() {
        for (int i = 0; i < TRANSACTION_TABLE_COLUMN_WIDTHS.length; i++) {
            TableColumn column = transactionTable.getColumnModel().getColumn(i);
            int width = (int) (transactionTable.getWidth() * TRANSACTION_TABLE_COLUMN_WIDTHS[i] / 100);
            column.setPreferredWidth(width);
        }
    }

    /**
     * Update the quick items table with the provided item DTOs.
     *
     * @param itemDtos The item DTOs.
     */
    public void updateQuickItems(@NonNull List<ItemDto> itemDtos) {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Updating quick items table with item DTOs: " + itemDtos);
        }
        quickItemDtos.addAll(itemDtos);
        loadQuickItemsPanel();
    }

    /**
     * Update the transaction metadata with the provided subtotal, discounts, taxes, and total.
     *
     * @param subtotal  The subtotal.
     * @param discounts The discounts.
     * @param taxes     The taxes.
     * @param total     The total.
     */
    public void updateTransactionMetadata(@NonNull BigDecimal subtotal, @NonNull BigDecimal discounts,
                                          @NonNull BigDecimal taxes, @NonNull BigDecimal total,
                                          @NonNull BigDecimal amountTendered, @NonNull BigDecimal changeDue) {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Updating metadata and totals");
        }
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        subtotalTextArea.setText("Subtotal: " + currencyFormat.format(subtotal));
        discountsTextArea.setText("Discounts: " + currencyFormat.format(discounts));
        taxesTextArea.setText("Taxes: " + currencyFormat.format(taxes));
        totalTextArea.setText("Total: " + currencyFormat.format(total));
        amountTenderedTextArea.setText("Amount Tendered: " + currencyFormat.format(amountTendered));
        changeDueTextArea.setText("Change Due: " + currencyFormat.format(changeDue));
    }

    /**
     * Reset the transaction metadata.
     */
    public void resetTransactionMetadata() {
        updateTransactionMetadata(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    /**
     * Update the transaction number.
     *
     * @param transactionNumber The transaction number.
     */
    public void updateTransactionNumber(int transactionNumber) {
        transactionNumberTextArea.setText("Transaction Number: " + transactionNumber);
    }

    /**
     * Show the transaction not started view.
     */
    public void showTransactionNotStarted() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Showing transaction not started");
        }
        bannerLabel.setText(String.format(WELCOME_BANNER_TEXT, storeName.toUpperCase()));
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(2, 1));
        contentPanel.add(new JLabel(NO_TRANSACTION_IN_PROGRESS_CONTENT_EXT, SwingConstants.CENTER));
        contentPanel.add(startTransactionButton);
        revalidate();
        repaint();
    }

    /**
     * Show the scanning in progress view.
     */
    public void showScanningInProgress() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Showing scanning in progress");
        }
        bannerLabel.setText(TRANSACTION_IN_PROGRESS_BANNER_TEXT);
        showFourRowView(loadTransactionsTablePanel(true), loadQuickItemsPanel(),
                loadMetadataPanel(), loadOnScanningBottomButtonsPanel());
    }

    /**
     * Show the awaiting cash payment view.
     */
    public void showAwaitingPayment() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Showing awaiting cash payment");
        }
        bannerLabel.setText(AWAITING_PAYMENT_BANNER_TEXT);
        showThreeRowView(loadTransactionsTablePanel(false), loadMetadataPanel(),
                loadPaymentButtonsPanel());
        revalidate();
        repaint();
    }

    /**
     * Show the transaction voided view.
     */
    public void showTransactionVoided() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Showing transaction voided");
        }
        bannerLabel.setText(TRANSACTION_VOIDED_BANNER_TEXT);
        JLabel voidedMessage = new JLabel(TRANSACTION_ENDED_TEXT, SwingConstants.CENTER);
        showContinue(voidedMessage);
    }

    /**
     * Show the transaction ended view.
     */
    public void showTransactionCompleted() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Showing transaction completed");
        }
        bannerLabel.setText(TRANSACTION_COMPLETED_BANNER_TEXT);
        JLabel completedMessage = new JLabel(TRANSACTION_COMPLETED_MESSAGE, SwingConstants.CENTER);
        showContinue(completedMessage);
    }

    /**
     * Show the continue view.
     *
     * @param continueMessage The voided message.
     */
    public void showContinue(JLabel continueMessage) {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Showing continue");
        }
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        JPanel continuePanel = new JPanel(new GridLayout(2, 1));
        continuePanel.add(continueMessage);
        continuePanel.add(continueButton);
        contentPanel.add(continuePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showThreeRowView(@NonNull JPanel p1, @NonNull JPanel p2, @NonNull JPanel p3) {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridy = 0;
        List<Double> weights = List.of(0.5, 0.2, 0.3);
        for (int i = 0; i < 3; i++) {
            JPanel p = i == 0 ? p1 : i == 1 ? p2 : p3;
            constraints.weighty = weights.get(i);
            contentPanel.add(p, constraints);
            constraints.gridy++;
        }
        revalidate();
        repaint();
    }

    private void showFourRowView(@NonNull JPanel p1, @NonNull JPanel p2, @NonNull JPanel p3, @NonNull JPanel p4) {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

        constraints.gridy = 0;
        List<Double> weights = List.of(0.5, 0.2, 0.2, 0.1);
        for (int i = 0; i < 4; i++) {
            JPanel p = i == 0 ? p1 : i == 1 ? p2 : i == 2 ? p3 : p4;
            constraints.weighty = weights.get(i);
            contentPanel.add(p, constraints);
            constraints.gridy++;
        }
        revalidate();
        repaint();
    }

    private void requestVoidButtonAction() {
        if (selectedLineItemUpcs.isEmpty()) {
            requestVoidTransaction();
        } else {
            requestVoidSelectedLineItems();
        }
    }

    private void requestVoidTransaction() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Requesting to void transaction");
        }
        if (JOptionPane.showConfirmDialog(CustomerView.this,
                "Are you sure you want to void this transaction?", "Void Transaction?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_VOID_TRANSACTION));
                clearTransactionsTableSelections();
            });
        }
    }

    private void requestVoidSelectedLineItems() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Requesting to void selected line items: " + selectedLineItemUpcs);
        }
        if (JOptionPane.showConfirmDialog(CustomerView.this,
                "Are you sure you want to void the selected line items?", "Void Line Items?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_VOID_LINE_ITEMS,
                        Map.of(ConstKeys.ITEM_UPCS, new HashSet<>(selectedLineItemUpcs))));
                clearTransactionsTableSelections();
            });
        }
    }

    private void clearTransactionsTableSelections() {
        selectedLineItemUpcs.clear();
        for (int row = 0; row < transactionTable.getRowCount(); row++) {
            transactionTable.setValueAt(false, row, 0);
        }
        updateVoidButtonText();
    }

    private void updateVoidButtonText() {
        if (selectedLineItemUpcs.isEmpty()) {
            voidButton.setText(VOID_TRANSACTION_BUTTON_TEXT);
        } else {
            voidButton.setText(VOID_LINE_ITEMS_BUTTON_TEXT);
        }
    }

    private void clearTransactionTableRows() {
        ((DefaultTableModel) transactionTable.getModel()).setRowCount(0);
    }

    private void addTransactionTableRow() {
        int rows = transactionTable.getRowCount();
        ((DefaultTableModel) transactionTable.getModel()).setRowCount(rows + 1);
    }

    private JPanel loadTransactionsTablePanel(boolean enabled) {
        transactionTable.setEnabled(enabled);
        JPanel panel = createScrollableTablePanel(transactionTable, TRANSACTION_TABLE_TITLE);
        panel.setPreferredSize(new Dimension(CUSTOMER_VIEW_FRAME_WIDTH, 300));
        if (enabled) {
            JButton clearSelectionsButton = new JButton("Clear Transaction Selections");
            clearSelectionsButton.setBackground(Color.YELLOW);
            clearSelectionsButton.setFont(STANDARD_BUTTON_FONT);
            clearSelectionsButton.addActionListener(e -> clearTransactionsTableSelections());
            panel.add(clearSelectionsButton, BorderLayout.SOUTH);
        }
        return panel;
    }

    private JPanel createScrollableTablePanel(@NonNull JTable table,
                                              @SuppressWarnings("SameParameterValue") @NonNull String tableTitle) {
        JScrollPane scrollPane = new JScrollPane(table);
        JLabel titleLabel = new JLabel(tableTitle, SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(16.0f));
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel loadQuickItemsPanel() {
        quickItemsPanel.removeAll();
        for (int i = 0; i < QUICK_ITEMS_ROWS_COUNT; i++) {
            for (int j = 0; j < QUICK_ITEMS_COLUMNS_COUNT; j++) {
                int index = (i * QUICK_ITEMS_COLUMNS_COUNT) + j;
                if (index >= quickItemDtos.size()) {
                    break;
                }
                ItemDto item = quickItemDtos.get(index);
                JButton button = createQuickItemButton(item);
                quickItemsPanel.add(button);
            }
        }
        return quickItemsPanel;
    }

    private JButton createQuickItemButton(@NonNull ItemDto item) {
        String itemName = item.getName();
        if (itemName.length() > QUICK_ITEM_TEXT_MAX_LENGTH) {
            itemName = itemName.substring(0, QUICK_ITEM_TEXT_MAX_LENGTH - 3) + "...";
        }
        JButton button = new JButton(itemName);
        button.setPreferredSize(new Dimension(STANDARD_BUTTON_WIDTH, STANDARD_BUTTON_HEIGHT));
        button.addActionListener(e -> {
            if (Application.DEBUG) {
                System.out.println("[CustomerView] Click on quick add item button: " + item.getName());
            }
            parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_ADD_ITEM,
                    Map.of(ConstKeys.ITEM_UPC, item.getUpc())));

        });
        return button;
    }

    private JPanel loadMetadataPanel() {
        JPanel metadataPanel = new JPanel(new GridLayout(1, 2));

        JPanel gridPane1 = new JPanel(new GridLayout(3, 1));
        gridPane1.add(storeNameTextArea);
        gridPane1.add(posInfoTextArea);
        gridPane1.add(transactionNumberTextArea);

        JPanel gridPane2 = new JPanel(new GridLayout(6, 1));
        gridPane2.add(subtotalTextArea);
        gridPane2.add(discountsTextArea);
        gridPane2.add(taxesTextArea);
        gridPane2.add(totalTextArea);
        gridPane2.add(amountTenderedTextArea);
        gridPane2.add(changeDueTextArea);

        metadataPanel.add(gridPane1);
        metadataPanel.add(gridPane2);

        return metadataPanel;
    }

    private JPanel loadOnScanningBottomButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 4));
        buttonsPanel.add(voidButton);
        buttonsPanel.add(openDiscountsButton);
        buttonsPanel.add(openScannerButton);
        buttonsPanel.add(openPoleDisplayButton);
        buttonsPanel.add(payWithCardButton);
        buttonsPanel.add(payWithCashButton);
        return buttonsPanel;
    }

    private JPanel loadPaymentButtonsPanel() {
        JPanel paymentButtonsPanel = new JPanel(new BorderLayout());
        paymentButtonsPanel.add(cancelPaymentButton);
        return paymentButtonsPanel;
    }
}

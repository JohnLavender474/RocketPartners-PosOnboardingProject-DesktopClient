package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.display.dto.LineItemDto;
import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The customer view of the POS system
 */
public class CustomerView extends JFrame {

    /**
     * A table model that represents the transactions table. This table model is used to display the transactions table
     * in the customer view. The first column is a checkbox column that allows the user to select line items to void.
     * The second column is the UPC of the item. The third column is the name of the item. The fourth column is the unit
     * price of the item. The fifth column is the quantity of the item. The sixth column is the total price of the item.
     * The table model is used to update the transactions table with line item DTOs.
     */
    public final class TransactionTableModel extends DefaultTableModel {

        /**
         * Constructor that initializes the transactions table model.
         */
        public TransactionTableModel() {
            super(new String[]{
                    TRANSACTION_TABLE_COLUMN_0_CHECKBOX,
                    TRANSACTION_TABLE_COLUMN_1_UPC,
                    TRANSACTION_TABLE_COLUMN_2_ITEM_NAME,
                    TRANSACTION_TABLE_COLUMN_3_PRICE,
                    TRANSACTION_TABLE_COLUMN_4_QUANTITY,
                    TRANSACTION_TABLE_COLUMN_5_TOTAL
            }, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0;
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
                String upc = (String) getValueAt(row, 1);
                if (checked) {
                    selectedLineItemUpcs.add(upc);
                    if (Application.DEBUG) {
                        System.out.println("[CustomerView] Select upc on row " + row + ": " + upc + ". Selected upc " +
                                "set size: " + selectedLineItemUpcs.size());
                    }
                } else {
                    selectedLineItemUpcs.remove(upc);
                    if (Application.DEBUG) {
                        System.out.println("[CustomerView] Deselect upc on row " + row + ": " + upc + ". Selected upc" +
                                "set size: " + selectedLineItemUpcs.size());
                    }}
                updateVoidButtonText();
            }
        }
    }

    private static final int CUSTOMER_VIEW_FRAME_WIDTH = 800;
    private static final int CUSTOMER_VIEW_FRAME_HEIGHT = 600;
    private static final int TRANSACTION_TABLE_WIDTH = 800;
    private static final int TRANSACTION_TABLE_HEIGHT = 200;
    private static final int PAYMENT_INFO_TABLE_GRID_ROWS_COUNT = 2;
    private static final int PAYMENT_INFO_TABLE_GRID_COLUMNS_COUNT = 1;

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
    private static final String CARD_NUMBER_LABEL_TEXT = "Card Number: ";
    private static final String CARD_PIN_NUMBER_LABEL_TEXT = "Card Pin Number: ";
    private static final String AMOUNT_TENDERED_LABEL_TEXT = "Amount Tendered: ";
    private static final String CHANGE_DUE_LABEL_TEXT = "Change Due: ";

    private static final String TRANSACTION_TABLE_COLUMN_0_CHECKBOX = "Select";
    private static final String TRANSACTION_TABLE_COLUMN_1_UPC = "UPC";
    private static final String TRANSACTION_TABLE_COLUMN_2_ITEM_NAME = "Item Name";
    private static final String TRANSACTION_TABLE_COLUMN_3_PRICE = "Unit Price";
    private static final String TRANSACTION_TABLE_COLUMN_4_QUANTITY = "Quantity";
    private static final String TRANSACTION_TABLE_COLUMN_5_TOTAL = "Total";

    private static final String START_TRANSACTION_BUTTON_TEXT = "Start Transaction";
    private static final String OPEN_SCANNER_BUTTON_TEXT = "Open Scanner";
    private static final String PAY_WITH_CARD_BUTTON_TEXT = "Pay with Card";
    private static final String PAY_WITH_CASH_BUTTON_TEXT = "Pay with Cash";
    private static final String VOID_TRANSACTION_BUTTON_TEXT = "Void Transaction";
    private static final String VOID_LINE_ITEMS_BUTTON_TEXT = "Void Line Items";
    private static final String CANCEL_PAYMENT_BUTTON_TEXT = "Cancel Payment";
    private static final String FINALIZE_BUTTON_TEXT = "Finalize";
    private static final String CONTINUE_BUTTON_TEXT = "Continue";

    @NonNull
    private final IPosEventDispatcher parentEventDispatcher;
    @NonNull
    private final String storeName;
    private final Set<String> selectedLineItemUpcs;

    private JLabel bannerLabel;

    private JPanel contentPanel;

    private JTextArea metadataArea;
    private JTextArea totalsArea;

    private JTable transactionTable;
    private JTable paymentInfoTable;

    private JTextArea amountTenderedArea;
    private JTextArea changeDueArea;
    private JTextArea cardNumberArea;
    private JTextArea cardPinNumberArea;

    private JButton startTransactionButton;
    private JButton payWithCardButton;
    private JButton payWithCashButton;
    private JButton voidButton;
    private JButton cancelPaymentButton;
    private JButton finalizeButton;
    private JButton continueButton;

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
        this.parentEventDispatcher = parentEventDispatcher;
        this.storeName = storeName;

        setSize(CUSTOMER_VIEW_FRAME_WIDTH, CUSTOMER_VIEW_FRAME_HEIGHT);
        setResizable(true);

        initializeComponents();

        setLayout(new BorderLayout());
        add(bannerLabel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(CustomerView.this,
                        "Are you sure you want to close this window?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });

        selectedLineItemUpcs = new HashSet<>();
    }

    private void initializeComponents() {
        bannerLabel = new JLabel("", SwingConstants.CENTER);
        contentPanel = new JPanel();

        TableModel transactionTableModel = new TransactionTableModel();
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setSize(TRANSACTION_TABLE_WIDTH, TRANSACTION_TABLE_HEIGHT);
        transactionTable.setRowSelectionAllowed(false);
        transactionTable.setColumnSelectionAllowed(false);
        transactionTable.setCellSelectionEnabled(false);

        metadataArea = new JTextArea();
        metadataArea.setEditable(false);
        totalsArea = new JTextArea();
        totalsArea.setEditable(false);

        paymentInfoTable = new JTable();
        TableModel paymentInfoTableModel = new DefaultTableModel(PAYMENT_INFO_TABLE_GRID_ROWS_COUNT,
                PAYMENT_INFO_TABLE_GRID_COLUMNS_COUNT);
        paymentInfoTable.setModel(paymentInfoTableModel);

        amountTenderedArea = new JTextArea();
        amountTenderedArea.setEditable(false);
        changeDueArea = new JTextArea();
        changeDueArea.setEditable(false);
        cardNumberArea = new JTextArea();
        cardNumberArea.setEditable(false);
        cardPinNumberArea = new JTextArea();
        cardPinNumberArea.setEditable(false);

        startTransactionButton = new JButton(START_TRANSACTION_BUTTON_TEXT);
        startTransactionButton.addActionListener(e ->
                parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_START_TRANSACTION)));

        payWithCardButton = new JButton(PAY_WITH_CARD_BUTTON_TEXT);
        payWithCardButton.addActionListener(e ->
                parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_PAY_WITH_CASH)));

        payWithCashButton = new JButton(PAY_WITH_CASH_BUTTON_TEXT);
        payWithCashButton.addActionListener(e ->
                parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_PAY_WITH_CARD)));

        JButton openScannerButton = new JButton(OPEN_SCANNER_BUTTON_TEXT);
        openScannerButton.addActionListener(e ->
                parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_OPEN_SCANNER)));

        voidButton = new JButton(VOID_TRANSACTION_BUTTON_TEXT);
        voidButton.addActionListener(e -> voidAction());

        cancelPaymentButton = new JButton(CANCEL_PAYMENT_BUTTON_TEXT);
        cancelPaymentButton.addActionListener(e ->
                parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_CANCEL_PAYMENT)));

        finalizeButton = new JButton(FINALIZE_BUTTON_TEXT);
        finalizeButton.addActionListener(e ->
                parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_COMPLETE_TRANSACTION)));

        continueButton = new JButton(CONTINUE_BUTTON_TEXT);
        continueButton.addActionListener(e ->
                parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_RESET_POS)));
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Setting visibility to: " + visible);
        }
    }

    /**
     * Update the transactions table with the provided line item DTOs.
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
            if (isSelected) {
                selectedLineItemUpcs.add(upc);
            }

            transactionTable.setValueAt(isSelected, row, 0);
            transactionTable.setValueAt(upc, row, 1);
            transactionTable.setValueAt(it.getItemName(), row, 2);
            transactionTable.setValueAt(it.getUnitPrice(), row, 3);
            transactionTable.setValueAt(it.getQuantity(), row, 4);

            BigDecimal total = it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity()));
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            String totalFormatted = currencyFormat.format(total);
            transactionTable.setValueAt(totalFormatted, row, 5);
        });
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
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(3, 1));
        addTransactionTableToContentPanel(true);
        addMetadataAndTotalsToContentPanel();
        addButtonsToContentPanel();
        revalidate();
        repaint();
    }

    /**
     * Show the awaiting cash payment view.
     */
    public void showAwaitingCashPayment() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Showing awaiting cash payment");
        }
        showAwaitingPayment();
        cardNumberArea.setText(CARD_NUMBER_LABEL_TEXT);
        cardPinNumberArea.setText(CARD_PIN_NUMBER_LABEL_TEXT);
        setAwaitingPaymentTableRow1Object(cardNumberArea);
        setAwaitingPaymentTableRow2Object(cardPinNumberArea);
    }

    /**
     * Show the awaiting card payment view.
     */
    public void showAwaitingCardPayment() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Showing awaiting card payment");
        }
        showAwaitingPayment();
        amountTenderedArea.setText(AMOUNT_TENDERED_LABEL_TEXT);
        changeDueArea.setText(CHANGE_DUE_LABEL_TEXT);
        setAwaitingPaymentTableRow1Object(amountTenderedArea);
        setAwaitingPaymentTableRow2Object(changeDueArea);
    }

    /**
     * Show the transaction voided view.
     */
    public void showTransactionVoided() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Showing transaction voided");
        }
        bannerLabel.setText(TRANSACTION_VOIDED_BANNER_TEXT);
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
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
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
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
        continueButton = new JButton(CONTINUE_BUTTON_TEXT);
        JPanel continuePanel = new JPanel(new GridLayout(2, 1));
        continuePanel.add(continueMessage);
        continuePanel.add(continueButton);
        contentPanel.add(continuePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showAwaitingPayment() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Showing awaiting payment");
        }
        bannerLabel.setText(AWAITING_PAYMENT_BANNER_TEXT);
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(3, 1));
        addTransactionTableToContentPanel(false);
        addMetadataAndTotalsToContentPanel();
        addPaymentInformationToContentPanel();
        revalidate();
        repaint();
    }

    private void voidAction() {
        if (selectedLineItemUpcs.isEmpty()) {
            voidTransaction();
        } else {
            voidSelectedLineItems();
        }
    }

    private void voidTransaction() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Voiding transaction");
        }
        parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_VOID_TRANSACTION));
        clearTransactionsTableSelections();
    }

    private void voidSelectedLineItems() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Voiding selected line items: " + selectedLineItemUpcs);
        }
        parentEventDispatcher.dispatchPosEvent(new PosEvent(PosEventType.REQUEST_VOID_LINE_ITEMS,
                Map.of(ConstKeys.LINE_ITEM_UPCS, Set.of(selectedLineItemUpcs))));
        clearTransactionsTableSelections();
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

    private void setAwaitingPaymentTableRow1Object(Object o) {
        paymentInfoTable.setValueAt(o, 0, 0);
    }

    private void setAwaitingPaymentTableRow2Object(Object o) {
        paymentInfoTable.setValueAt(o, 1, 0);
    }

    private void clearTransactionTableRows() {
        ((DefaultTableModel) transactionTable.getModel()).setRowCount(0);
    }

    private void addTransactionTableRow() {
        int rows = transactionTable.getRowCount();
        ((DefaultTableModel) transactionTable.getModel()).setRowCount(rows + 1);
    }

    private void setTransactionTableColumnNames(String[] columnNames) {
        ((DefaultTableModel) transactionTable.getModel()).setColumnIdentifiers(columnNames);
    }

    private void addTransactionTableToContentPanel(boolean enabled) {
        contentPanel.add(new JScrollPane(transactionTable));
        transactionTable.setEnabled(enabled);
    }

    private void addMetadataAndTotalsToContentPanel() {
        JPanel metadataAndTotalsPanel = new JPanel(new GridLayout(1, 2));
        metadataAndTotalsPanel.add(new JScrollPane(metadataArea));
        metadataAndTotalsPanel.add(new JScrollPane(totalsArea));
        contentPanel.add(metadataAndTotalsPanel);
    }

    private void addButtonsToContentPanel() {
        JPanel otherButtonsPanel = new JPanel(new GridLayout(1, 2));
        otherButtonsPanel.add(voidButton);
        otherButtonsPanel.add(cancelPaymentButton);

        JPanel paymentButtonsPanel = new JPanel(new GridLayout(1, 2));
        paymentButtonsPanel.add(payWithCardButton);
        paymentButtonsPanel.add(payWithCashButton);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel.add(otherButtonsPanel);
        buttonsPanel.add(paymentButtonsPanel);

        contentPanel.add(buttonsPanel);
    }

    private void addPaymentInformationToContentPanel() {
        JPanel paymentPanel = new JPanel(new GridLayout(1, 2));
        paymentPanel.add(paymentInfoTable);

        JPanel paymentButtonsPanel = new JPanel(new GridLayout(1, 2));
        paymentButtonsPanel.add(cancelPaymentButton);
        paymentButtonsPanel.add(finalizeButton);

        paymentPanel.add(paymentButtonsPanel);
        contentPanel.add(paymentPanel);
    }
}

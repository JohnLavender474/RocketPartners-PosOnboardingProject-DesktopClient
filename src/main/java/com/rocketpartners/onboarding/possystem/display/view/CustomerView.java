package com.rocketpartners.onboarding.possystem.display.view;

import com.rocketpartners.onboarding.possystem.Application;
import lombok.NonNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;

public class CustomerView extends JFrame {

    private static final int CUSTOMER_VIEW_FRAME_WIDTH = 800;
    private static final int CUSTOMER_VIEW_FRAME_HEIGHT = 600;
    private static final int TRANSACTION_TABLE_WIDTH = 800;
    private static final int TRANSACTION_TABLE_HEIGHT = 200;
    private static final int TRANSACTION_TABLE_GRID_ROWS_VARIABLE_COUNT = 0;
    private static final int TRANSACTION_TABLE_GRID_COLUMNS_COUNT = 5;
    private static final int PAYMENT_INFO_TABLE_GRID_ROWS_COUNT = 2;
    private static final int PAYMENT_INFO_TABLE_GRID_COLUMNS_COUNT = 1;

    private static final String WELCOME_BANNER_TEXT = "WELCOME TO %s";
    private static final String NO_TRANSACTION_IN_PROGRESS_CONTENT_EXT = "PLEASE SCAN AN ITEM TO BEGIN A TRANSACTION.";
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

    private static final String TRANSACTION_TABLE_COLUMN_1_UPC = "UPC";
    private static final String TRANSACTION_TABLE_COLUMN_2_ITEM_NAME = "Item Name";
    private static final String TRANSACTION_TABLE_COLUMN_3_PRICE = "Unit Price";
    private static final String TRANSACTION_TABLE_COLUMN_4_QUANTITY = "Quantity";
    private static final String TRANSACTION_TABLE_COLUMN_5_TOTAL = "Total";

    private static final String PAY_WITH_CARD_BUTTON_TEXT = "Pay with Card";
    private static final String PAY_WITH_CASH_BUTTON_TEXT = "Pay with Cash";
    private static final String VOID_BUTTON_TEXT = "Void";
    private static final String CANCEL_PAYMENT_BUTTON_TEXT = "Cancel Payment";
    private static final String FINALIZE_BUTTON_TEXT = "Finalize";
    private static final String CONTINUE_BUTTON_TEXT = "Continue";

    private final String storeName;

    private JLabel bannerLabel;
    private JPanel contentPanel;
    private JTable transactionTable;
    private JTextArea metadataArea;
    private JTextArea totalsArea;
    private JTable paymentInfoTable;
    private JTextArea amountTenderedArea;
    private JTextArea changeDueArea;
    private JTextArea cardNumberArea;
    private JTextArea cardPinNumberArea;
    private JButton payWithCardButton;
    private JButton payWithCashButton;
    private JButton voidButton;
    private JButton cancelPaymentButton;
    private JButton finalizeButton;
    private JButton continueButton;

    public CustomerView(@NonNull String storeName, int posLane) {
        super("Customer View - Lane " + posLane);
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Creating Customer View for Store Name: " + storeName + " and Lane: " + posLane);
        }
        this.storeName = storeName;
        setSize(CUSTOMER_VIEW_FRAME_WIDTH, CUSTOMER_VIEW_FRAME_HEIGHT);
        setResizable(true);
        initializeComponents();
        setLayout(new BorderLayout());
        add(bannerLabel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Setting visibility to: " + visible);
        }
    }

    private void initializeComponents() {
        bannerLabel = new JLabel("", SwingConstants.CENTER);
        contentPanel = new JPanel();
        TableModel transactionTableModel = new DefaultTableModel(TRANSACTION_TABLE_GRID_ROWS_VARIABLE_COUNT,
                TRANSACTION_TABLE_GRID_COLUMNS_COUNT);
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setSize(TRANSACTION_TABLE_WIDTH, TRANSACTION_TABLE_HEIGHT);
        setTransactionTableColumnNames(new String[]{
                TRANSACTION_TABLE_COLUMN_1_UPC,
                TRANSACTION_TABLE_COLUMN_2_ITEM_NAME,
                TRANSACTION_TABLE_COLUMN_3_PRICE, TRANSACTION_TABLE_COLUMN_4_QUANTITY,
                TRANSACTION_TABLE_COLUMN_5_TOTAL
        });
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
        payWithCardButton = new JButton(PAY_WITH_CARD_BUTTON_TEXT);
        payWithCashButton = new JButton(PAY_WITH_CASH_BUTTON_TEXT);
        voidButton = new JButton(VOID_BUTTON_TEXT);
        cancelPaymentButton = new JButton(CANCEL_PAYMENT_BUTTON_TEXT);
        finalizeButton = new JButton(FINALIZE_BUTTON_TEXT);
        continueButton = new JButton(CONTINUE_BUTTON_TEXT);
    }

    public void showTransactionNotStarted() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Showing transaction not started");
        }
        bannerLabel.setText(String.format(WELCOME_BANNER_TEXT, storeName.toUpperCase()));
        contentPanel.removeAll();
        contentPanel.add(new JLabel(NO_TRANSACTION_IN_PROGRESS_CONTENT_EXT));
        revalidate();
        repaint();
    }

    public void showScanningInProgress() {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Showing scanning in progress");
        }
        bannerLabel.setText(TRANSACTION_IN_PROGRESS_BANNER_TEXT);
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(3, 1));
        addTransactionTableToContentPanel(true);
        addMetadataAndTotalsToContentPanel();
        addPaymentAndVoidButtonsToContentPanel();
        revalidate();
        repaint();
    }

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

    public void showContinue(JLabel voidedMessage) {
        if (Application.DEBUG) {
            System.out.println("[CustomerView] Showing continue");
        }
        continueButton = new JButton(CONTINUE_BUTTON_TEXT);
        JPanel voidedPanel = new JPanel(new GridLayout(2, 1));
        voidedPanel.add(voidedMessage);
        voidedPanel.add(continueButton);
        contentPanel.add(voidedPanel, BorderLayout.CENTER);
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

    private void addPaymentAndVoidButtonsToContentPanel() {
        JPanel paymentButtonsPanel = new JPanel(new GridLayout(1, 2));
        paymentButtonsPanel.add(payWithCardButton);
        paymentButtonsPanel.add(payWithCashButton);
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel.add(voidButton);
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

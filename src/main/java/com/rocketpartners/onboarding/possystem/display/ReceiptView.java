package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.Application;
import com.rocketpartners.onboarding.possystem.display.dto.LineItemDto;
import com.rocketpartners.onboarding.possystem.display.dto.TransactionDto;
import lombok.NonNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;

/**
 * View for the receipt. This class is responsible for displaying the receipt based on the transaction DTO.
 */
public class ReceiptView extends JFrame {

    private static final int MIN_WIDTH = 750;
    private static final int MIN_HEIGHT = 500;
    private static final int ROWS = 4;
    private static final int COLUMNS = 1;
    private static final String UPC_COLUMN = "UPC";
    private static final String NAME_COLUMN = "Name";
    private static final String QUANTITY_COLUMN = "Quantity";
    private static final int POS_INFO_ROWS = 3;
    private static final int POS_INFO_COLUMNS = 1;

    private final JTable lineItemsTable;

    private final JTextArea storeNameArea;
    private final JTextArea posInfoArea;
    private final JTextArea transactionNumberTextArea;

    private final JTextArea subtotalTextArea;
    private final JTextArea discountsTextArea;
    private final JTextArea taxesTextArea;
    private final JTextArea totalTextArea;

    private final JTextArea amountTenderedTextArea;
    private final JTextArea changeDueTextArea;

    /**
     * Constructor that accepts a title.
     *
     * @param title The title of the frame.
     */
    public ReceiptView(@NonNull String title) {
        super(title);

        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setLayout(new GridLayout(ROWS, COLUMNS));

        lineItemsTable = new JTable(new DefaultTableModel(new String[]{
                UPC_COLUMN, NAME_COLUMN, QUANTITY_COLUMN}, 0));
        lineItemsTable.setShowGrid(true);
        lineItemsTable.setEnabled(false);
        lineItemsTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(lineItemsTable);
        add(scrollPane);

        JPanel posInfoPanel = new JPanel(new GridLayout(POS_INFO_ROWS, POS_INFO_COLUMNS));
        storeNameArea = new JTextArea();
        storeNameArea.setEditable(false);
        posInfoPanel.add(storeNameArea);
        posInfoArea = new JTextArea();
        posInfoArea.setEditable(false);
        posInfoPanel.add(posInfoArea);
        transactionNumberTextArea = new JTextArea();
        transactionNumberTextArea.setEditable(false);
        posInfoPanel.add(transactionNumberTextArea);
        add(posInfoPanel);

        JPanel totalsPanel = new JPanel(new GridLayout(4, 1));
        subtotalTextArea = new JTextArea();
        subtotalTextArea.setEditable(false);
        totalsPanel.add(subtotalTextArea);
        discountsTextArea = new JTextArea();
        discountsTextArea.setEditable(false);
        totalsPanel.add(discountsTextArea);
        taxesTextArea = new JTextArea();
        taxesTextArea.setEditable(false);
        totalsPanel.add(taxesTextArea);
        totalTextArea = new JTextArea();
        totalTextArea.setEditable(false);
        totalsPanel.add(totalTextArea);
        add(totalsPanel);

        JPanel tenderedPanel = new JPanel(new GridLayout(2, 1));
        amountTenderedTextArea = new JTextArea();
        amountTenderedTextArea.setEditable(false);
        tenderedPanel.add(amountTenderedTextArea);
        changeDueTextArea = new JTextArea();
        changeDueTextArea.setEditable(false);
        tenderedPanel.add(changeDueTextArea);
        add(tenderedPanel);
    }

    /**
     * Updates the receipt view based on the transaction DTO.
     *
     * @param transactionDto The transaction DTO.
     */
    public void update(@NonNull TransactionDto transactionDto) {
        if (Application.DEBUG) {
            System.out.println("[ReceiptView] Updating with transaction DTO: " + transactionDto);
        }

        storeNameArea.setText(transactionDto.getStoreName());
        posInfoArea.setText("POS Lane: " + transactionDto.getPosLane());
        transactionNumberTextArea.setText("Transaction Number: " + transactionDto.getTransactionNumber());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        subtotalTextArea.setText("Subtotal: " + currencyFormat.format(transactionDto.getSubtotal()));
        discountsTextArea.setText("Discounts: " + currencyFormat.format(transactionDto.getDiscounts()));
        taxesTextArea.setText("Taxes: " + currencyFormat.format(transactionDto.getTaxes()));
        totalTextArea.setText("Total: " + currencyFormat.format(transactionDto.getTotal()));

        amountTenderedTextArea.setText("Amount Tendered: " + currencyFormat.format(transactionDto.getAmountTendered()));
        changeDueTextArea.setText("Change Due: " + currencyFormat.format(transactionDto.getChangeDue()));

        buildLineItemsTable(transactionDto.getLineItemDtos());

        revalidate();
        repaint();
    }

    /**
     * Builds the line items table based on the line item DTOs.
     *
     * @param lineItemDtos The line item DTOs.
     */
    public void buildLineItemsTable(@NonNull List<LineItemDto> lineItemDtos) {
        if (Application.DEBUG) {
            System.out.println("[ReceiptView] Building table with line items: " + lineItemDtos);
        }

        clearLineItemsTableRows();

        lineItemDtos.forEach(it -> {
            addLineItemsTableRow();
            int row = lineItemsTable.getRowCount() - 1;
            String upc = it.getItemUpc();
            lineItemsTable.setValueAt(upc, row, 0);
            lineItemsTable.setValueAt(it.getItemName(), row, 1);
            lineItemsTable.setValueAt(it.getQuantity(), row, 2);
        });
    }

    private void clearLineItemsTableRows() {
        ((DefaultTableModel) lineItemsTable.getModel()).setRowCount(0);
    }

    private void addLineItemsTableRow() {
        int rows = lineItemsTable.getRowCount();
        ((DefaultTableModel) lineItemsTable.getModel()).setRowCount(rows + 1);
    }
}

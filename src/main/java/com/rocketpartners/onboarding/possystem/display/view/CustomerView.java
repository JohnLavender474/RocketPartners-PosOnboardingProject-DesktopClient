package com.rocketpartners.onboarding.possystem.display.view;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class CustomerView extends JFrame {

    private JLabel bannerLabel;
    private JPanel contentPanel;
    private JTable transactionTable;
    private JTextArea metadataArea;
    private JTextArea totalsArea;
    private JButton payWithCardButton;
    private JButton payWithCashButton;
    private JButton voidButton;
    private JButton cancelPaymentButton;
    private JButton finalizeButton;
    private JButton continueButton;

    public CustomerView() {
        super("Customer View");
        initializeComponents();
        setLayout(new BorderLayout());
        add(bannerLabel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void initializeComponents() {
        bannerLabel = new JLabel("", SwingConstants.CENTER);
        contentPanel = new JPanel();
        transactionTable = new JTable();
        metadataArea = new JTextArea();
        totalsArea = new JTextArea();
        payWithCardButton = new JButton("Pay with Card");
        payWithCashButton = new JButton("Pay with Cash");
        voidButton = new JButton("Void");
        cancelPaymentButton = new JButton("Cancel Payment");
        finalizeButton = new JButton("Finalize");
        continueButton = new JButton("Continue");
    }

    public void showTransactionNotStarted() {
        bannerLabel.setText("No Transaction In Progress");
        contentPanel.removeAll();
        contentPanel.add(new JLabel("NO TRANSACTION IN PROGRESS. PLEASE SCAN AN ITEM TO START."));
        revalidate();
        repaint();
    }

    public void showScanningInProgress() {
        bannerLabel.setText("Transaction In Progress");
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(3, 1));

        // Row 1: Transaction Table
        contentPanel.add(new JScrollPane(transactionTable));

        // Row 2: Metadata and Totals
        JPanel metadataAndTotalsPanel = new JPanel(new GridLayout(1, 2));
        metadataAndTotalsPanel.add(new JScrollPane(metadataArea));
        metadataAndTotalsPanel.add(new JScrollPane(totalsArea));
        contentPanel.add(metadataAndTotalsPanel);

        // Row 3: Buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        JPanel payButtonsPanel = new JPanel(new GridLayout(1, 2));
        payButtonsPanel.add(payWithCardButton);
        payButtonsPanel.add(payWithCashButton);
        buttonsPanel.add(payButtonsPanel);
        buttonsPanel.add(voidButton);
        contentPanel.add(buttonsPanel);

        revalidate();
        repaint();
    }

    public void showAwaitingPayment() {
        // TODO: Implement this method to handle either card or cash payment depending on user selection
    }

    public void showAwaitingCardPayment() {
        bannerLabel.setText("Awaiting Card Payment");
        showPaymentComponents("Card Number: \nPIN: ");
    }

    public void showAwaitingCashPayment() {
        bannerLabel.setText("Awaiting Cash Payment");
        showPaymentComponents("Amount Tendered: \nAmount Due: ");
    }

    private void showPaymentComponents(String paymentText) {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(3, 1));

        // Row 1: Transaction Table (read-only)
        transactionTable.setEnabled(false);
        contentPanel.add(new JScrollPane(transactionTable));

        // Row 2: Metadata and Totals (read-only)
        JPanel metadataAndTotalsPanel = new JPanel(new GridLayout(1, 2));
        metadataAndTotalsPanel.add(new JScrollPane(metadataArea));
        metadataAndTotalsPanel.add(new JScrollPane(totalsArea));
        contentPanel.add(metadataAndTotalsPanel);

        // Row 3: Cash Payment Information
        JPanel paymentPanel = new JPanel(new GridLayout(1, 2));
        JTextArea paymentInfo = new JTextArea(paymentText);

        paymentInfo.setEditable(false);
        paymentPanel.add(paymentInfo);
        JPanel paymentButtonsPanel = new JPanel(new GridLayout(1, 2));
        paymentButtonsPanel.add(cancelPaymentButton);
        paymentButtonsPanel.add(finalizeButton);
        paymentPanel.add(paymentButtonsPanel);
        contentPanel.add(paymentPanel);

        revalidate();
        repaint();
    }

    public void showTransactionVoided() {
        bannerLabel.setText("Transaction Voided");
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel voidedMessage = new JLabel("TRANSACTION ENDED", SwingConstants.CENTER);
        showContinue(voidedMessage);
    }

    public void showTransactionCompleted() {
        bannerLabel.setText("Transaction Completed");
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JLabel completedMessage = new JLabel("We appreciate your business! Your receipt is now displayed on the " +
                "receipt display", SwingConstants.CENTER);
        showContinue(completedMessage);
    }

    private void showContinue(JLabel voidedMessage) {
        continueButton = new JButton("CONTINUE");

        JPanel voidedPanel = new JPanel(new GridLayout(2, 1));
        voidedPanel.add(voidedMessage);
        voidedPanel.add(continueButton);

        contentPanel.add(voidedPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }
}

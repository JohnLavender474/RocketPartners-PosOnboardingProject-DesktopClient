package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.event.IPosEventDispatcher;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class ReceiptView extends JFrame {

    private static final int MIN_WIDTH = 675;
    private static final int MIN_HEIGHT = 200;

    private final IPosEventDispatcher parentEventDispatcher;

    private JTextArea titleArea;
    private JTable transactionTable;
    private JTextArea storeNameArea;
    private JTextArea posInfoArea;
    private JTextArea transactionTextArea;

    public ReceiptView(@NonNull String frameTitle, @NonNull IPosEventDispatcher parentEventDispatcher) {
        super(frameTitle);
        this.parentEventDispatcher = parentEventDispatcher;

        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

        JScrollPane scrollPane = new JScrollPane();
    }

}

package com.rocketpartners.onboarding.possystem.display;

import com.rocketpartners.onboarding.possystem.constant.ConstKeys;
import com.rocketpartners.onboarding.possystem.display.dto.TransactionDto;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import lombok.NonNull;

import java.util.Set;

/**
 * Controller for the receipt view. This class is responsible for updating the receipt view based on POS events.
 */
public class ReceiptViewController implements IController {

    private final Set<PosEventType> eventsToListenFor = Set.of(
            PosEventType.TRANSACTION_COMPLETED,
            PosEventType.TRANSACTION_STARTED,
            PosEventType.TRANSACTION_VOIDED,
            PosEventType.POS_RESET,
            PosEventType.POS_BOOTUP
    );

    private final ReceiptView receiptView;

    /**
     * Constructor that accepts a frame title and a parent POS event dispatcher.
     *
     * @param frameTitle The title of the frame.
     */
    public ReceiptViewController(@NonNull String frameTitle) {
        receiptView = new ReceiptView(frameTitle);
    }

    /**
     * Constructor for testing.
     *
     * @param receiptView The receipt view.
     */
    ReceiptViewController(@NonNull ReceiptView receiptView) {
        this.receiptView = receiptView;
    }

    @Override
    public void onPosEvent(@NonNull PosEvent event) {
        switch (event.getType()) {
            case TRANSACTION_COMPLETED -> {
                TransactionDto transactionDto = event.getProperty(ConstKeys.TRANSACTION_DTO, TransactionDto.class);
                receiptView.update(transactionDto);
                receiptView.setVisible(true);
            }
            case TRANSACTION_STARTED, TRANSACTION_VOIDED, POS_RESET, POS_BOOTUP -> receiptView.setVisible(false);
        }
    }

    @Override
    public void dispatchPosEvent(@NonNull PosEvent event) {
        // do nothing
    }

    @Override
    public @NonNull Set<PosEventType> getEventTypesToListenFor() {
        return eventsToListenFor;
    }
}

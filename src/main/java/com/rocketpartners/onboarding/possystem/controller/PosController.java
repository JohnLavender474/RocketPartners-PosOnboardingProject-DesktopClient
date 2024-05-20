package com.rocketpartners.onboarding.possystem.controller;

import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.IPosEventManager;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.factory.TransactionFactory;
import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.model.Transaction;
import lombok.Getter;
import lombok.NonNull;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@Getter
public class PosController implements IController, IPosEventManager {

    private final PosSystem posSystem;
    private final Queue<PosEvent> posEventQueue;
    private final Set<IPosEventListener> posEventListeners;

    private Transaction transaction;
    private TransactionState transactionState;
    private int transactionNumber;

    public PosController(@NonNull PosSystem posSystem) {
        this.posSystem = posSystem;
        posEventQueue = new LinkedList<>();
        posEventListeners = new LinkedHashSet<>();
        transactionState = TransactionState.NOT_STARTED;
        transactionNumber = 1;
    }

    @Override
    public void bootUp() {
        transaction = null;
        transactionState = TransactionState.NOT_STARTED;
    }

    @Override
    public void update() {
        while (!posEventQueue.isEmpty()) {
            PosEvent event = posEventQueue.poll();
            handlePosEvent(event);
            for (IPosEventListener listener : posEventListeners) {
                listener.onPosEvent(event);
            }
        }
    }

    /**
     * Start a new transaction. Package-private for testing purposes.
     */
    void startTransaction() {
        transaction = TransactionFactory.getInstance().createTransaction(posSystem.getId(), transactionNumber);
        transactionNumber++;
        transactionState = TransactionState.SCANNING_IN_PROGRESS;
    }

    @Override
    public void shutdown() {
        transaction = null;
        transactionState = TransactionState.NOT_STARTED;
    }

    @Override
    public void dispatchPosEvent(@NonNull PosEvent event) {
        posEventQueue.add(event);
    }

    @Override
    public void registerPosEventListener(@NonNull IPosEventListener listener) {
        posEventListeners.add(listener);
    }

    @Override
    public void unregisterPosEventListener(@NonNull IPosEventListener listener) {
        posEventListeners.remove(listener);
    }

    private void handlePosEvent(@NonNull PosEvent event) {
        // TODO: Implement this method
        /*
        switch (event.getType()) {
            case TRANSACTION_START:
                startTransaction();
                break;
            case TRANSACTION_END:
                endTransaction();
                break;
            case ITEM_SCAN:
                scanItem(event);
                break;
            case ITEM_REMOVE:
                removeItem(event);
                break;
            case ITEM_QUANTITY_CHANGE:
                changeItemQuantity(event);
                break;
            default:
                break;
        }
         */
    }
}

package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.possystem.constant.TransactionState;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.IPosEventManager;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import com.rocketpartners.onboarding.possystem.factory.TransactionFactory;
import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.model.Transaction;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Controller for a POS system. This class is responsible for handling POS events and managing transactions.
 */
@Component
public class PosComponent implements IComponent, IPosEventManager {

    private static final Logger logger = LoggerFactory.getLogger(PosComponent.class);

    private final TransactionFactory transactionFactory;
    private final Map<PosEventType, List<PosEvent>> events;
    private final Set<IPosEventListener> posEventListeners;

    @Getter
    private PosSystem posSystem;
    @Getter
    private Transaction transaction;
    @Getter
    private TransactionState transactionState;
    @Getter
    private int transactionNumber;

    /**
     * Constructor that accepts a transaction factory. The method {@link #setPosSystem} must be called before the
     * {@link #bootUp()} method is called or else an {@link IllegalStateException} will be thrown.
     *
     * @param transactionFactory The transaction factory.
     */
    @Autowired
    public PosComponent(@NonNull TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
        events = new EnumMap<>(PosEventType.class);
        posEventListeners = new LinkedHashSet<>();
        transactionState = TransactionState.NOT_STARTED;
        transactionNumber = 1;
    }

    /**
     * Set the POS system for this component.
     *
     * @param posSystem The POS system.
     */
    public void setPosSystem(@NonNull PosSystem posSystem) {
        this.posSystem = posSystem;
    }

    @Override
    public void bootUp() {
        if (posSystem == null) {
            throw new IllegalStateException("POS system must be set to non-null value before calling bootUp()");
        }
        transaction = null;
        transactionState = TransactionState.NOT_STARTED;
        dispatchPosEvent(new PosEvent(PosEventType.POS_BOOTUP, Map.of("posSystemId", posSystem.getId())));
    }

    @Override
    public void update() {
        posEventListeners.forEach(listener -> {
            Set<PosEventType> eventTypesToListenFor = listener.getEventTypesToListenFor();
            eventTypesToListenFor.forEach(type -> {
                List<PosEvent> eventsOfType = events.get(type);
                if (eventsOfType != null) {
                    eventsOfType.forEach(listener::onPosEvent);
                }
            });
        });
        events.clear();
    }

    /**
     * Start a new transaction. Package-private for testing purposes.
     */
    void startTransaction() {
        transaction = transactionFactory.createAndPersist(posSystem.getId(), transactionNumber);
        transactionNumber++;
        transactionState = TransactionState.SCANNING_IN_PROGRESS;
        dispatchPosEvent(new PosEvent(PosEventType.TRANSACTION_STARTED));
    }

    /**
     * Void the current transaction. Package-private for testing purposes.
     */
    void voidTransaction() {
        transactionState = TransactionState.VOIDED;
        dispatchPosEvent(new PosEvent(PosEventType.TRANSACTION_VOIDED));
    }

    /**
     * Complete the current transaction. Package-private for testing purposes.
     */
    void completeTransaction() {
        transactionState = TransactionState.COMPLETED;
        dispatchPosEvent(new PosEvent(PosEventType.TRANSACTION_COMPLETED));
    }

    /**
     * Resets the pos component to its initial state. Package-private for testing purposes.
     */
    void resetPos() {
        transaction = null;
        transactionState = TransactionState.NOT_STARTED;
        dispatchPosEvent(new PosEvent(PosEventType.POS_RESET));
    }

    @Override
    public void shutdown() {
        transaction = null;
        transactionState = TransactionState.NOT_STARTED;
        events.clear();
        posEventListeners.clear();
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this implementation, this {@link PosComponent} instance listens and acts on the event immediately. However,
     * {@link IPosEventListener} instances do not receive the event until the {@link #update()} method call is made.
     *
     * @param event The event to dispatch.
     */
    @Override
    public void dispatchPosEvent(@NonNull PosEvent event) {
        handlePosEvent(event);

        PosEventType type = event.getType();
        List<PosEvent> eventsOfType = events.getOrDefault(type, new ArrayList<>());
        eventsOfType.add(event);
        events.put(type, eventsOfType);
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
        switch (event.getType()) {
            case REQUEST_START_TRANSACTION -> {
                if (transactionState != TransactionState.NOT_STARTED) {
                    logger.error("Request to start transaction not allowed when transaction state is not NOT_STARTED");
                    return;
                }
                startTransaction();
            }

            case REQUEST_VOID_TRANSACTION -> {
                if (transactionState == TransactionState.NOT_STARTED) {
                    logger.error("Request to void transaction not allowed when transaction state is NOT_STARTED");
                    return;
                }
                voidTransaction();
            }

            case REQUEST_COMPLETE_TRANSACTION -> {
                if (transactionState != TransactionState.AWAITING_PAYMENT) {
                    logger.error("Request to complete transaction not allowed when transaction state is not " +
                            "AWAITING_PAYMENT");
                    return;
                }
                completeTransaction();
            }

            case REQUEST_RESET_POS -> {
                if (transactionState != TransactionState.VOIDED && transactionState != TransactionState.COMPLETED) {
                    logger.error("Request to reset POS not allowed when transaction state is not VOIDED or COMPLETED");
                    return;
                }
                resetPos();
            }
        }
    }
}

package com.rocketpartners.onboarding.possystem.component.journal;

import com.rocketpartners.onboarding.possystem.component.IComponent;
import com.rocketpartners.onboarding.possystem.event.IPosEventListener;
import com.rocketpartners.onboarding.possystem.event.PosEvent;
import com.rocketpartners.onboarding.possystem.event.PosEventType;
import com.rocketpartners.onboarding.possystem.utils.LogFormatter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.EnumSet;
import java.util.Set;

/**
 * A journal listener that writes {@link PosEventType#LOG} and {@link PosEventType#ERROR} event logs to a socket.
 */
@RequiredArgsConstructor
public class RemoteJournal implements IPosEventListener, IComponent {

    private final String host;
    private final int port;

    private Socket socket;
    private PrintWriter out;

    @Override
    public void bootUp() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to server on " + host + ":" + port);
        } catch (Exception e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    @Override
    public @NonNull Set<PosEventType> getEventTypesToListenFor() {
        return EnumSet.of(PosEventType.LOG, PosEventType.ERROR);
    }

    @Override
    public void onPosEvent(@NonNull PosEvent event) {
        if (out == null) {
            return;
        }
        String message = event.getProperty("message", String.class);
        switch (event.getType()) {
            case LOG:
                out.println(LogFormatter.formatLog(message));
                break;
            case ERROR:
                out.println(LogFormatter.formatError(message));
                break;
        }
    }

    @Override
    public void shutDown() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}

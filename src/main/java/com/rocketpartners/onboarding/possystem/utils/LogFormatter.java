package com.rocketpartners.onboarding.possystem.utils;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Formats logs.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LogFormatter {

    private static final String format = "[%s] %s: %s";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Returns a formatted log message.
     *
     * @param message the message to format
     * @return the formatted log message
     */
    public static String formatLog(@NonNull String message) {
        return format("LOG", message);
    }

    /**
     * Returns a formatted error message.
     *
     * @param message the message to format
     * @return the formatted error message
     */
    public static String formatError(@NonNull String message) {
        return format("ERROR", message);
    }

    private static String format(@NonNull String type, @NonNull String message) {
        String currentDateTime = dateFormat.format(new Date());
        return String.format(format, currentDateTime, type, message);
    }
}

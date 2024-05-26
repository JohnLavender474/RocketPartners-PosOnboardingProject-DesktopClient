package com.rocketpartners.onboarding.possystem.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UtilMethods {

    /**
     * Checks if the input is a double.
     *
     * @param input The input to check.
     * @return True if the input is a number, false otherwise.
     */
    public static boolean isDouble(@NonNull String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

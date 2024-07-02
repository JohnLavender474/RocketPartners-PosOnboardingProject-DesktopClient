package com.rocketpartners.onboarding.possystem.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

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

    /**
     * Checks if the elements of the second list are contained in the first list in order.
     * @param i1 The first list.
     * @param i2 The second list.
     * @return True if the elements of the second list are contained in the first list in order, false otherwise.
     * @param <T> The type of the elements in the lists.
     */
    public static <T> boolean containsInOrder(@NonNull List<T> i1, @NonNull List<T> i2) {
        int i2Index = 0;
        for (T t : i1) {
            if (i2Index >= i2.size()) {
                break;
            }

            T other = i2.get(i2Index);

            if (t == null) {
                if (other == null) {
                    i2Index++;
                }
                continue;
            }

            if (t.equals(other)) {
                i2Index++;
            }
        }
        return i2Index >= i2.size();
    }
}

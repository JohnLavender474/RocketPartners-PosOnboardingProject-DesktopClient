package com.rocketpartners.onboarding.possystem.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for reading TSV files.
 */
public class TsvFileReader {

    /**
     * Read a TSV file and return the data as a list of string arrays.
     *
     * @param filePath the path to the TSV file
     * @return the data as a list of string arrays
     * @throws RuntimeException if an error occurs while reading the file
     */
    public List<String[]> read(String filePath) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t");
                data.add(fields);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}

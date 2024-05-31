package com.rocketpartners.onboarding.possystem.repository.mysql;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the connection to the database
 */
@RequiredArgsConstructor
public class DatabaseConnectionManager {

    private final String url;
    private final String username;
    private final String password;

    /**
     * Start a new connection to the database
     *
     * @return the new connection
     */
    public Connection startNewConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("Failed to start a new connection: " + e.getMessage());
            throw new RuntimeException("Failed to start a new connection", e);
        }
    }
}

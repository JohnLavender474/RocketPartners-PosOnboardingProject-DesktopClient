package com.rocketpartners.onboarding.possystem.repository.mysql;

import java.sql.*;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Initializes the MySQL database for the POS system. This class is a singleton and should be used to create the
 * database andd tables if they don't already exist.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MySQLDatabaseInitializer {

    private static MySQLDatabaseInitializer instance;

    /**
     * Gets the instance of the MySQL database initializer.
     *
     * @return the instance of the MySQL database initializer
     */
    public static MySQLDatabaseInitializer getInstance() {
        if (instance == null) {
            instance = new MySQLDatabaseInitializer();
        }
        return instance;
    }

    /**
     * Initializes the MySQL database for the POS system. This method creates the database and tables if they don't
     * already exist.
     *
     * @param url      the URL of the MySQL server
     * @param dbName   the name of the database
     * @param username the username to connect to the database
     * @param password the password to connect to the database
     */
    public void initializeDatabase(@NonNull String url, @NonNull String dbName, @NonNull String username,
                                   @NonNull String password) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {

            // Create the database if it doesn't exist
            String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + dbName;
            statement.executeUpdate(createDatabaseSQL);

            // Use the newly created or existing database
            String useDatabaseSQL = "USE " + dbName;
            statement.executeUpdate(useDatabaseSQL);

            // Create the items table if it doesn't exist
            String createItemsTableSQL = "CREATE TABLE IF NOT EXISTS items (" +
                    "    upc VARCHAR(255) PRIMARY KEY, " +
                    "    name VARCHAR(255) NOT NULL, " +
                    "    unit_price DECIMAL(10, 2) NOT NULL, " +
                    "    category VARCHAR(255), " +
                    "    description TEXT" +
                    ")";
            statement.executeUpdate(createItemsTableSQL);

            // Create the discounts table if it doesn't exist
            String createDiscountsTableSQL = "CREATE TABLE IF NOT EXISTS discounts (" +
                    "    id VARCHAR(255) PRIMARY KEY," +
                    "    type VARCHAR(50) NOT NULL," +
                    "    value DECIMAL(10, 2)," +
                    "    description TEXT," +
                    "    min_quantity INT DEFAULT 1," +
                    "    discounted_quantity INT DEFAULT 0," +
                    "    discounted_value DECIMAL(10, 2)," +
                    "    applicable_category VARCHAR(255)," +
                    "    applicable_upcs TEXT" +
                    ")";
            statement.executeUpdate(createDiscountsTableSQL);

            // Create the pos sytems table if it doesn't exist
            String createPosSystemsTableSQL = "CREATE TABLE IF NOT EXISTS pos_systems (" +
                    "    id VARCHAR(255) PRIMARY KEY, " +
                    "    store_name VARCHAR(255) NOT NULL, " +
                    "    pos_lane INT NOT NULL" +
                    ")";
            statement.executeUpdate(createPosSystemsTableSQL);

            // Create the transactions table if it doesn't exist
            String createTransactionsTableSQL = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "    id VARCHAR(255) PRIMARY KEY," +
                    "    pos_system_id VARCHAR(255) NOT NULL," +
                    "    transaction_number INT NOT NULL," +
                    "    subtotal DECIMAL(10, 2) NOT NULL," +
                    "    taxes DECIMAL(10, 2) NOT NULL," +
                    "    discounts DECIMAL(10, 2) NOT NULL," +
                    "    total DECIMAL(10, 2) NOT NULL," +
                    "    amount_tendered DECIMAL(10, 2) NOT NULL," +
                    "    change_due DECIMAL(10, 2) NOT NULL," +
                    "    customer_id VARCHAR(255)," +
                    "    voided BOOLEAN NOT NULL," +
                    "    tendered BOOLEAN NOT NULL," +
                    "    time_created TIMESTAMP NOT NULL," +
                    "    time_completed TIMESTAMP," +
                    "    FOREIGN KEY (pos_system_id) REFERENCES pos_systems(id)" +
                    ")";
            statement.executeUpdate(createTransactionsTableSQL);

            String createLineItemsTableSQL = "CREATE TABLE IF NOT EXISTS line_items (" +
                    "    transaction_id VARCHAR(255) NOT NULL," +
                    "    item_upc VARCHAR(255) NOT NULL," +
                    "    quantity INT NOT NULL," +
                    "    voided BOOLEAN NOT NULL," +
                    "    PRIMARY KEY (transaction_id, item_upc)," +
                    "    FOREIGN KEY (transaction_id) REFERENCES transactions(id)," +
                    "    FOREIGN KEY (item_upc) REFERENCES items(upc)" +
                    ")";
            statement.executeUpdate(createLineItemsTableSQL);

            System.out.println("Database and tables checked/created successfully!");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            throw new RuntimeException("Error initializing database", e);
        }
    }
}

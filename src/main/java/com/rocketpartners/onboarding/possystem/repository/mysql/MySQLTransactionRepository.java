package com.rocketpartners.onboarding.possystem.repository.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.rocketpartners.onboarding.possystem.model.LineItem;
import com.rocketpartners.onboarding.possystem.model.Transaction;
import com.rocketpartners.onboarding.possystem.repository.TransactionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MySQLTransactionRepository implements TransactionRepository {

    private final DatabaseConnectionManager connectionManager;

    @Override
    public void saveTransaction(@NonNull Transaction transaction) {
        String sql = "INSERT INTO transactions (id, pos_system_id, transaction_number, subtotal, taxes, discounts, " +
                "total, amount_tendered, change_due, customer_id, voided, tendered, time_created, time_completed) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE pos_system_id = VALUES" +
                "(pos_system_id), transaction_number = VALUES(transaction_number), subtotal = VALUES(subtotal), taxes" +
                " = VALUES(taxes), discounts = VALUES(discounts), total = VALUES(total), amount_tendered = VALUES" +
                "(amount_tendered), change_due = VALUES(change_due), customer_id = VALUES(customer_id), voided = " +
                "VALUES(voided), tendered = VALUES(tendered), time_created = VALUES(time_created), time_completed = " +
                "VALUES(time_completed)";

        try (Connection connection = connectionManager.startNewConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, transaction.getId());
            stmt.setString(2, transaction.getPosSystemId());
            stmt.setInt(3, transaction.getTransactionNumber());
            stmt.setBigDecimal(4, transaction.getSubtotal());
            stmt.setBigDecimal(5, transaction.getTaxes());
            stmt.setBigDecimal(6, transaction.getDiscounts());
            stmt.setBigDecimal(7, transaction.getTotal());
            stmt.setBigDecimal(8, transaction.getAmountTendered());
            stmt.setBigDecimal(9, transaction.getChangeDue());
            stmt.setString(10, transaction.getCustomerId());
            stmt.setBoolean(11, transaction.isVoided());
            stmt.setBoolean(12, transaction.isTendered());
            stmt.setTimestamp(13, Timestamp.valueOf(transaction.getTimeCreated()));
            if (transaction.getTimeCompleted() != null) {
                stmt.setTimestamp(14, Timestamp.valueOf(transaction.getTimeCompleted()));
            } else {
                stmt.setNull(14, Types.TIMESTAMP);
            }
            stmt.executeUpdate();

            // Save line items
            String lineItemSql = "INSERT INTO line_items (transaction_id, item_upc, quantity, voided) " +
                    "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE quantity = VALUES(quantity), voided = VALUES(voided)";
            try (PreparedStatement lineItemStmt = connection.prepareStatement(lineItemSql)) {
                for (LineItem lineItem : transaction.getLineItems()) {
                    lineItemStmt.setString(1, lineItem.getTransactionId());
                    lineItemStmt.setString(2, lineItem.getItemUpc());
                    lineItemStmt.setInt(3, lineItem.getQuantity());
                    lineItemStmt.setBoolean(4, lineItem.isVoided());
                    lineItemStmt.addBatch();
                }
                lineItemStmt.executeBatch();
            }
        } catch (SQLException e) {
            System.err.println("Failed to save transaction: " + e.getMessage());
        }
    }

    @Override
    public Transaction getTransactionById(@NonNull String id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTransaction(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to get transaction by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteTransactionById(@NonNull String id) {
        String transactionSql = "DELETE FROM transactions WHERE id = ?";
        String lineItemSql = "DELETE FROM line_items WHERE transaction_id = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement transactionStmt =
                connection.prepareStatement(transactionSql);
             PreparedStatement lineItemStmt = connection.prepareStatement(lineItemSql)) {
            lineItemStmt.setString(1, id);
            lineItemStmt.executeUpdate();

            transactionStmt.setString(1, id);
            transactionStmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete transaction by ID: " + e.getMessage());
        }
    }

    @Override
    public boolean transactionExists(@NonNull String id) {
        String sql = "SELECT 1 FROM transactions WHERE id = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Failed to check if transaction exists: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Transaction> getTransactionsByCustomerId(@NonNull String customerId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE customer_id = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to get transactions by customer ID: " + e.getMessage());
        }
        return transactions;
    }

    @Override
    public List<Transaction> getTransactionsByPosSystemId(@NonNull String posSystemId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE pos_system_id = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, posSystemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to get transactions by POS system ID: " + e.getMessage());
        }
        return transactions;
    }

    private Transaction mapResultSetToTransaction(@NonNull ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getString("id"),
                rs.getString("pos_system_id"),
                getLineItemsForTransaction(rs.getString("id")),
                rs.getInt("transaction_number"),
                rs.getBigDecimal("subtotal"),
                rs.getBigDecimal("taxes"),
                rs.getBigDecimal("discounts"),
                rs.getBigDecimal("total"),
                rs.getBigDecimal("amount_tendered"),
                rs.getBigDecimal("change_due"),
                rs.getString("customer_id"),
                rs.getBoolean("voided"),
                rs.getBoolean("tendered"),
                rs.getTimestamp("time_created").toLocalDateTime(),
                rs.getTimestamp("time_completed").toLocalDateTime()
        );
    }

    private List<LineItem> getLineItemsForTransaction(@NonNull String transactionId) {
        List<LineItem> lineItems = new ArrayList<>();
        String sql = "SELECT * FROM line_items WHERE transaction_id = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, transactionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LineItem lineItem = new LineItem(
                            rs.getString("item_upc"),
                            rs.getString("transaction_id"),
                            rs.getInt("quantity"),
                            rs.getBoolean("voided")
                    );
                    lineItems.add(lineItem);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to get line items for transaction: " + e.getMessage());
        }
        return lineItems;
    }
}


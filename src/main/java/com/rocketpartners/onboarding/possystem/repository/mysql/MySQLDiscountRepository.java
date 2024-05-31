package com.rocketpartners.onboarding.possystem.repository.mysql;

import java.sql.*;
import java.util.*;

import com.rocketpartners.onboarding.possystem.model.Discount;
import com.rocketpartners.onboarding.possystem.repository.DiscountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * MySQL implementation of the DiscountRepository interface.
 */
@RequiredArgsConstructor
public class MySQLDiscountRepository implements DiscountRepository {

    private final DatabaseConnectionManager connectionManager;

    @Override
    public void saveDiscount(@NonNull Discount discount) {
        if (discount.getId() == null || discount.getId().isBlank()) {
            String uuid = UUID.randomUUID().toString();
            discount.setId(uuid);
        }

        String sql = "INSERT INTO discounts (id, type, value, description, min_quantity, discounted_quantity, " +
                "discounted_value, applicable_category, applicable_upcs) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON " +
                "DUPLICATE KEY UPDATE type = VALUES(type), value = VALUES(value), description = VALUES(description), " +
                "min_quantity = VALUES(min_quantity), discounted_quantity = VALUES(discounted_quantity), " +
                "discounted_value = VALUES(discounted_value), applicable_category = VALUES(applicable_category), " +
                "applicable_upcs = VALUES(applicable_upcs)";

        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, discount.getId());
            stmt.setString(2, discount.getType());
            stmt.setBigDecimal(3, discount.getValue());
            stmt.setString(4, discount.getDescription());
            stmt.setInt(5, discount.getMinQuantity());
            stmt.setInt(6, discount.getDiscountedQuantity());
            stmt.setBigDecimal(7, discount.getDiscountedValue());
            stmt.setString(8, discount.getApplicableCategory());
            stmt.setString(9, String.join(",", discount.getApplicableUpcs()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving discount: " + e.getMessage());
        }
    }

    @Override
    public List<Discount> getAllDiscounts() {
        List<Discount> discounts = new ArrayList<>();
        String sql = "SELECT * FROM discounts";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                discounts.add(mapResultSetToDiscount(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all discounts: " + e.getMessage());
        }
        return discounts;
    }

    @Override
    public void deleteDiscountById(@NonNull String id) {
        String sql = "DELETE FROM discounts WHERE id = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting discount: " + e.getMessage());
        }
    }

    @Override
    public Discount getDiscountById(@NonNull String id) {
        String sql = "SELECT * FROM discounts WHERE id = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDiscount(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting discount by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean discountExists(@NonNull String id) {
        String sql = "SELECT 1 FROM discounts WHERE id = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking if discount exists: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Discount> getDiscountsByType(@NonNull String type) {
        List<Discount> discounts = new ArrayList<>();
        String sql = "SELECT * FROM discounts WHERE type = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    discounts.add(mapResultSetToDiscount(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting discounts by type: " + e.getMessage());
        }
        return discounts;
    }

    @Override
    public List<Discount> getDiscountsByApplicableCategory(@NonNull String category) {
        List<Discount> discounts = new ArrayList<>();
        String sql = "SELECT * FROM discounts WHERE applicable_category = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    discounts.add(mapResultSetToDiscount(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting discounts by category: " + e.getMessage());
        }
        return discounts;
    }

    @Override
    public List<Discount> getDiscountsByApplicableUpc(@NonNull String upc) {
        List<Discount> discounts = new ArrayList<>();
        String sql = "SELECT * FROM discounts WHERE FIND_IN_SET(?, applicable_upcs)";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, upc);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    discounts.add(mapResultSetToDiscount(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting discounts by UPC: " + e.getMessage());
        }
        return discounts;
    }

    private Discount mapResultSetToDiscount(@NonNull ResultSet rs) throws SQLException {
        String upcs = rs.getString("applicable_upcs");
        Set<String> upcSet = new HashSet<>();
        if (upcs != null && !upcs.isEmpty()) {
            upcSet.addAll(Arrays.asList(upcs.split(",")));
        }

        return new Discount(
                rs.getString("id"),
                rs.getString("type"),
                rs.getBigDecimal("value"),
                rs.getString("description"),
                rs.getInt("min_quantity"),
                rs.getInt("discounted_quantity"),
                rs.getBigDecimal("discounted_value"),
                rs.getString("applicable_category"),
                upcSet
        );
    }
}


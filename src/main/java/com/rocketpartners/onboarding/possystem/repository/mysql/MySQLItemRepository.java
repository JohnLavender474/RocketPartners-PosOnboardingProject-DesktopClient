package com.rocketpartners.onboarding.possystem.repository.mysql;

import com.rocketpartners.onboarding.possystem.model.Item;
import com.rocketpartners.onboarding.possystem.repository.ItemRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of the {@link ItemRepository} interface.
 */
@RequiredArgsConstructor
public class MySQLItemRepository implements ItemRepository {

    private final DatabaseConnectionManager connectionManager;

    @Override
    public void saveItem(@NonNull Item item) {
        String sql = "INSERT INTO items (upc, name, unit_price, category, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, item.getUpc());
            stmt.setString(2, item.getName());
            stmt.setBigDecimal(3, item.getUnitPrice());
            stmt.setString(4, item.getCategory());
            stmt.setString(5, item.getDescription());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving item: " + e.getMessage());
        }
    }

    @Override
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT upc, name, unit_price, category, description FROM items";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(new Item(
                        rs.getString("upc"),
                        rs.getString("name"),
                        rs.getBigDecimal("unit_price"),
                        rs.getString("category"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all items: " + e.getMessage());
        }
        return items;
    }

    @Override
    public void deleteItemByUpc(@NonNull String upc) {
        String sql = "DELETE FROM items WHERE upc = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, upc);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting item: " + e.getMessage());
        }
    }

    @Override
    public Item getItemByUpc(@NonNull String upc) {
        String sql = "SELECT upc, name, unit_price, category, description FROM items WHERE upc = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, upc);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Item(
                            rs.getString("upc"),
                            rs.getString("name"),
                            rs.getBigDecimal("unit_price"),
                            rs.getString("category"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting item by UPC: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean itemExists(@NonNull String upc) {
        String sql = "SELECT 1 FROM items WHERE upc = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, upc);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking if item exists: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Item> getItemsByName(@NonNull String name) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT upc, name, unit_price, category, description FROM items WHERE name LIKE ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new Item(
                            rs.getString("upc"),
                            rs.getString("name"),
                            rs.getBigDecimal("unit_price"),
                            rs.getString("category"),
                            rs.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting items by name: " + e.getMessage());
        }
        return items;
    }

    @Override
    public List<Item> getItemsByCategory(@NonNull String category) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT upc, name, unit_price, category, description FROM items WHERE category = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new Item(
                            rs.getString("upc"),
                            rs.getString("name"),
                            rs.getBigDecimal("unit_price"),
                            rs.getString("category"),
                            rs.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting items by category: " + e.getMessage());
        }
        return items;
    }
}

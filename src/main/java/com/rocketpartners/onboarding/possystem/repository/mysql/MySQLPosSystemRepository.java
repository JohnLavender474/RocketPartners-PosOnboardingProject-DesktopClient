package com.rocketpartners.onboarding.possystem.repository.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.rocketpartners.onboarding.possystem.model.PosSystem;
import com.rocketpartners.onboarding.possystem.repository.PosSystemRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MySQLPosSystemRepository implements PosSystemRepository {

    private final DatabaseConnectionManager connectionManager;

    @Override
    public void savePosSystem(@NonNull PosSystem posSystem) {
        if (posSystem.getId() == null || posSystem.getId().isBlank()) {
            String uuid = UUID.randomUUID().toString();
            posSystem.setId(uuid);
        }

        String sql = "INSERT INTO pos_systems (id, store_name, pos_lane) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE " +
                "store_name = VALUES(store_name), pos_lane = VALUES(pos_lane)";

        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, posSystem.getId());
            stmt.setString(2, posSystem.getStoreName());
            stmt.setInt(3, posSystem.getPosLane());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to save POS system: " + e.getMessage());
        }
    }

    @Override
    public PosSystem getPosSystemById(@NonNull String id) {
        String sql = "SELECT * FROM pos_systems WHERE id = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPosSystem(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to get POS system by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deletePosSystemById(@NonNull String id) {
        String sql = "DELETE FROM pos_systems WHERE id = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete POS system by ID: " + e.getMessage());
        }
    }

    @Override
    public boolean posSystemExists(@NonNull String id) {
        String sql = "SELECT 1 FROM pos_systems WHERE id = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Failed to check if POS system exists: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<PosSystem> getAllPosSystems() {
        List<PosSystem> posSystems = new ArrayList<>();
        String sql = "SELECT * FROM pos_systems";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                posSystems.add(mapResultSetToPosSystem(rs));
            }
        } catch (SQLException e) {
            System.err.println("Failed to get all POS systems: " + e.getMessage());
        }
        return posSystems;
    }

    @Override
    public List<PosSystem> getPosSystemsByStoreName(@NonNull String storeName) {
        List<PosSystem> posSystems = new ArrayList<>();
        String sql = "SELECT * FROM pos_systems WHERE store_name = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, storeName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posSystems.add(mapResultSetToPosSystem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to get POS systems by store name: " + e.getMessage());
        }
        return posSystems;
    }

    @Override
    public PosSystem getPosSystemByStoreNameAndPosLane(@NonNull String storeName, int posLane) {
        String sql = "SELECT * FROM pos_systems WHERE store_name = ? AND pos_lane = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, storeName);
            stmt.setInt(2, posLane);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPosSystem(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to get POS system by store name and POS lane: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean posSystemExistsByStoreNameAndPosLane(@NonNull String storeName, int posLane) {
        String sql = "SELECT 1 FROM pos_systems WHERE store_name = ? AND pos_lane = ?";
        try (Connection connection = connectionManager.startNewConnection(); PreparedStatement stmt =
                connection.prepareStatement(sql)) {
            stmt.setString(1, storeName);
            stmt.setInt(2, posLane);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Failed to check if POS system exists by store name and POS lane: " + e.getMessage());
        }
        return false;
    }

    private PosSystem mapResultSetToPosSystem(@NonNull ResultSet rs) throws SQLException {
        return new PosSystem(
                rs.getString("id"),
                rs.getString("store_name"),
                rs.getInt("pos_lane")
        );
    }
}


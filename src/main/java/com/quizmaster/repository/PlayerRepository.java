package com.quizmaster.repository;

import com.quizmaster.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access repository for the players table.
 * Reuses existing player records by unique player_name.
 */
public class PlayerRepository {

    private final DatabaseConnection databaseConnection;

    public PlayerRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection != null ? databaseConnection : new DatabaseConnection();
    }

    /**
     * Finds an existing player_id by name or inserts a new player record within the given connection/transaction.
     */
    public int findOrCreate(Connection conn, String playerName) throws SQLException {
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Anonymous Player";
        }
        String cleanName = playerName.trim();

        // 1. Query for existing player
        String selectSql = "SELECT player_id FROM players WHERE player_name = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, cleanName);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("player_id");
                }
            }
        }

        // 2. Insert new player if not found
        String insertSql = "INSERT INTO players (player_name) VALUES (?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, cleanName);
            insertStmt.executeUpdate();

            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        throw new SQLException("Failed to retrieve generated player_id for: " + cleanName);
    }

    /**
     * Standalone convenience method that finds or creates a player using an isolated connection.
     */
    public int findOrCreate(String playerName) throws SQLException {
        try (Connection conn = databaseConnection.getConnection()) {
            return findOrCreate(conn, playerName);
        }
    }

    public String findPlayerName(int playerId) throws SQLException {
        String sql = "SELECT player_name FROM players WHERE player_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("player_name");
                }
            }
        }
        return null;
    }

    public List<String> getAllPlayerNames() throws SQLException {
        List<String> names = new ArrayList<>();
        String sql = "SELECT player_name FROM players ORDER BY player_name ASC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                names.add(rs.getString("player_name"));
            }
        }
        return names;
    }
}

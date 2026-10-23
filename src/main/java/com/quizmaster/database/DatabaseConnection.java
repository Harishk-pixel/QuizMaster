package com.quizmaster.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages JDBC connections, connectivity validation, and safe error reporting.
 */
public class DatabaseConnection {

    public static class ConnectionStatus {
        private final boolean connected;
        private final String message;
        private final long responseTimeMs;

        public ConnectionStatus(boolean connected, String message, long responseTimeMs) {
            this.connected = connected;
            this.message = message;
            this.responseTimeMs = responseTimeMs;
        }

        public boolean isConnected() {
            return connected;
        }

        public String getMessage() {
            return message;
        }

        public long getResponseTimeMs() {
            return responseTimeMs;
        }
    }

    private final DatabaseConfig config;

    public DatabaseConnection() {
        this(new DatabaseConfig());
    }

    public DatabaseConnection(DatabaseConfig config) {
        this.config = config != null ? config : new DatabaseConfig();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
    }

    /**
     * Non-destructive connectivity verification that never exposes credentials.
     */
    public ConnectionStatus testConnection() {
        long start = System.currentTimeMillis();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("SELECT 1");
            long elapsed = System.currentTimeMillis() - start;
            return new ConnectionStatus(true, "Connected to MySQL successfully.", elapsed);

        } catch (SQLException e) {
            long elapsed = System.currentTimeMillis() - start;
            String cleanMsg = sanitizeErrorMessage(e.getMessage());
            return new ConnectionStatus(false, "Database connection unavailable: " + cleanMsg, elapsed);
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            return new ConnectionStatus(false, "Unexpected database error: " + e.getClass().getSimpleName(), elapsed);
        }
    }

    public DatabaseConfig getConfig() {
        return config;
    }

    private String sanitizeErrorMessage(String msg) {
        if (msg == null) return "Unknown database error.";
        if (msg.contains("Communications link failure")) {
            return "Cannot reach MySQL server at " + config.getUrl() + ". Verify that MySQL is running.";
        }
        if (msg.contains("Access denied for user")) {
            return "Authentication failed for user '" + config.getUsername() + "'. Check QUIZ_DB_USER / QUIZ_DB_PASSWORD.";
        }
        if (msg.contains("Unknown database")) {
            return "Database 'quizmaster_db' does not exist. Please create it using schema.sql.";
        }
        return msg;
    }
}

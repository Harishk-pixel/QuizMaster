package com.quizmaster.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Initializes database schemas from schema.sql idempotently without dropping data.
 */
public class DatabaseInitializer {

    private final DatabaseConnection databaseConnection;

    public DatabaseInitializer(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection != null ? databaseConnection : new DatabaseConnection();
    }

    public boolean initializeDatabase() {
        DatabaseConnection.ConnectionStatus status = databaseConnection.testConnection();
        if (!status.isConnected()) {
            return false;
        }

        try (Connection conn = databaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String schemaSql = loadSchemaSql();
            // Remove single-line comments (-- ...)
            String cleanSql = schemaSql.replaceAll("(?m)^\\s*--.*$", "");
            String[] statements = cleanSql.split(";");

            for (String sql : statements) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    try {
                        stmt.execute(trimmed);
                    } catch (Exception ex) {
                        // Tolerate index already exists in dialects where IF NOT EXISTS on index differs
                        if (!ex.getMessage().toLowerCase().contains("already exists")) {
                            System.err.println("Warning executing schema statement: " + ex.getMessage());
                        }
                    }
                }
            }
            return true;

        } catch (Exception e) {
            System.err.println("Database schema initialization encountered an error: " + e.getMessage());
            return false;
        }
    }

    private String loadSchemaSql() {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = getClass().getResourceAsStream("/db/schema.sql")) {
            if (is != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                }
            }
        } catch (Exception ignored) {
        }

        if (sb.length() == 0) {
            // Fallback inline schema definitions
            return "CREATE TABLE IF NOT EXISTS players (" +
                    "player_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "player_name VARCHAR(100) NOT NULL UNIQUE, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);\n" +
                    "CREATE TABLE IF NOT EXISTS quiz_attempts (" +
                    "attempt_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "player_id INT NOT NULL, " +
                    "category VARCHAR(50) NOT NULL, " +
                    "difficulty VARCHAR(20) NOT NULL, " +
                    "total_questions INT NOT NULL, " +
                    "correct_answers INT NOT NULL, " +
                    "incorrect_answers INT NOT NULL, " +
                    "unanswered_questions INT NOT NULL, " +
                    "score INT NOT NULL, " +
                    "percentage INT NOT NULL, " +
                    "time_taken_seconds INT NOT NULL, " +
                    "completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);\n" +
                    "CREATE TABLE IF NOT EXISTS quiz_answer_details (" +
                    "detail_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "attempt_id INT NOT NULL, " +
                    "question_id INT NOT NULL, " +
                    "question_text VARCHAR(1000) NOT NULL, " +
                    "selected_answer VARCHAR(255) NULL, " +
                    "correct_answer VARCHAR(255) NOT NULL, " +
                    "is_correct BOOLEAN NOT NULL, " +
                    "explanation TEXT);\n";
        }

        return sb.toString();
    }
}

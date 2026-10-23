package com.quizmaster.repository;

import com.quizmaster.database.DatabaseConnection;
import com.quizmaster.model.Category;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.QuizAttempt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access repository for the quiz_attempts table.
 */
public class QuizAttemptRepository {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final DatabaseConnection databaseConnection;

    public QuizAttemptRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection != null ? databaseConnection : new DatabaseConnection();
    }

    /**
     * Inserts a quiz attempt within an active transaction and returns the generated attempt_id.
     */
    public int insertAttempt(Connection conn, int playerId, Category category, Difficulty difficulty,
                              int totalQuestions, int correctAnswers, int incorrectAnswers,
                              int unansweredQuestions, int score, int percentage,
                              int timeTakenSeconds, Timestamp completedAt) throws SQLException {

        String sql = "INSERT INTO quiz_attempts (" +
                "player_id, category, difficulty, total_questions, correct_answers, " +
                "incorrect_answers, unanswered_questions, score, percentage, " +
                "time_taken_seconds, completed_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, playerId);
            stmt.setString(2, category != null ? category.name() : Category.PROGRAMMING.name());
            stmt.setString(3, difficulty != null ? difficulty.name() : Difficulty.MEDIUM.name());
            stmt.setInt(4, totalQuestions);
            stmt.setInt(5, correctAnswers);
            stmt.setInt(6, incorrectAnswers);
            stmt.setInt(7, unansweredQuestions);
            stmt.setInt(8, score);
            stmt.setInt(9, percentage);
            stmt.setInt(10, timeTakenSeconds);
            stmt.setTimestamp(11, completedAt != null ? completedAt : new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Failed to obtain generated attempt_id for quiz attempt.");
    }

    public List<QuizAttempt> findAll(int limit) throws SQLException {
        String sql = "SELECT a.*, p.player_name FROM quiz_attempts a " +
                "JOIN players p ON a.player_id = p.player_id " +
                "ORDER BY a.completed_at DESC, a.attempt_id DESC LIMIT ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Math.max(1, limit));
            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSetToAttempts(rs);
            }
        }
    }

    public List<QuizAttempt> findByCategory(Category category, int limit) throws SQLException {
        if (category == null) {
            return findAll(limit);
        }

        String sql = "SELECT a.*, p.player_name FROM quiz_attempts a " +
                "JOIN players p ON a.player_id = p.player_id " +
                "WHERE a.category = ? " +
                "ORDER BY a.completed_at DESC, a.attempt_id DESC LIMIT ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.name());
            stmt.setInt(2, Math.max(1, limit));
            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSetToAttempts(rs);
            }
        }
    }

    public List<QuizAttempt> findByPlayer(String playerName, int limit) throws SQLException {
        if (playerName == null || playerName.trim().isEmpty()) {
            return findAll(limit);
        }

        String sql = "SELECT a.*, p.player_name FROM quiz_attempts a " +
                "JOIN players p ON a.player_id = p.player_id " +
                "WHERE p.player_name = ? " +
                "ORDER BY a.completed_at DESC, a.attempt_id DESC LIMIT ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerName.trim());
            stmt.setInt(2, Math.max(1, limit));
            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSetToAttempts(rs);
            }
        }
    }

    public QuizAttempt findById(int attemptId) throws SQLException {
        String sql = "SELECT a.*, p.player_name FROM quiz_attempts a " +
                "JOIN players p ON a.player_id = p.player_id " +
                "WHERE a.attempt_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<QuizAttempt> list = mapResultSetToAttempts(rs);
                return list.isEmpty() ? null : list.get(0);
            }
        }
    }

    public boolean deleteAttempt(int attemptId) throws SQLException {
        String sql = "DELETE FROM quiz_attempts WHERE attempt_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            return stmt.executeUpdate() > 0;
        }
    }

    private List<QuizAttempt> mapResultSetToAttempts(ResultSet rs) throws SQLException {
        List<QuizAttempt> attempts = new ArrayList<>();
        while (rs.next()) {
            int dbId = rs.getInt("attempt_id");
            String displayId = "QM-" + dbId;
            String playerName = rs.getString("player_name");

            Timestamp ts = rs.getTimestamp("completed_at");
            String dateStr = (ts != null) ? ts.toLocalDateTime().format(DATE_FORMATTER) : "N/A";

            String catStr = rs.getString("category");
            Category category = Category.fromDisplayName(catStr);

            String diffStr = rs.getString("difficulty");
            Difficulty difficulty = Difficulty.fromDisplayName(diffStr);

            int total = rs.getInt("total_questions");
            int correct = rs.getInt("correct_answers");
            int incorrect = rs.getInt("incorrect_answers");
            int unanswered = rs.getInt("unanswered_questions");
            int score = rs.getInt("score");
            int percentage = rs.getInt("percentage");
            int timeTaken = rs.getInt("time_taken_seconds");

            attempts.add(new QuizAttempt(
                    dbId,
                    displayId,
                    playerName,
                    dateStr,
                    category,
                    difficulty,
                    score,
                    total,
                    percentage,
                    correct,
                    incorrect,
                    unanswered,
                    timeTaken
            ));
        }
        return attempts;
    }
}

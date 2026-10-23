package com.quizmaster.service;

import com.quizmaster.database.DatabaseConnection;
import com.quizmaster.model.Category;
import com.quizmaster.model.QuizAttempt;
import com.quizmaster.model.QuizResult;
import com.quizmaster.repository.PlayerRepository;
import com.quizmaster.repository.QuizAnswerRepository;
import com.quizmaster.repository.QuizAttemptRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

/**
 * Service coordinating transactional persistence of quiz attempts,
 * player associations, question answer details, and historical data retrieval.
 */
public class QuizPersistenceService {

    private final DatabaseConnection databaseConnection;
    private final PlayerRepository playerRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAnswerRepository quizAnswerRepository;

    public QuizPersistenceService(DatabaseConnection databaseConnection, QuestionBankService questionBankService) {
        this.databaseConnection = databaseConnection != null ? databaseConnection : new DatabaseConnection();
        this.playerRepository = new PlayerRepository(this.databaseConnection);
        this.quizAttemptRepository = new QuizAttemptRepository(this.databaseConnection);
        this.quizAnswerRepository = new QuizAnswerRepository(this.databaseConnection, questionBankService);
    }

    /**
     * Transactionally saves a quiz result into MySQL.
     * Guarantees all-or-nothing atomicity and rolls back on any error.
     */
    public synchronized boolean saveQuizResult(QuizResult result) {
        if (result == null) {
            return false;
        }

        // Prevent duplicate database insertions if already persisted
        if (result.isPersisted() && result.getDatabaseAttemptId() > 0) {
            return true;
        }

        Connection conn = null;
        try {
            conn = databaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Find or create player
            int playerId = playerRepository.findOrCreate(conn, result.getPlayerName());

            // 2. Insert quiz attempt record
            int attemptId = quizAttemptRepository.insertAttempt(
                    conn,
                    playerId,
                    result.getCategory(),
                    result.getDifficulty(),
                    result.getTotalQuestions(),
                    result.getCorrectCount(),
                    result.getIncorrectCount(),
                    result.getUnansweredCount(),
                    result.getScore(),
                    result.getPercentage(),
                    result.getTimeTakenSeconds(),
                    new Timestamp(System.currentTimeMillis())
            );

            // 3. Insert answer details for all questions
            quizAnswerRepository.insertAnswerDetails(conn, attemptId, result.getReviewItems());

            // 4. Commit transaction
            conn.commit();

            // 5. Update result with success status
            result.setPersisted(true);
            result.setDatabaseAttemptId(attemptId);
            result.setPersistenceMessage("Saved permanently to database (Attempt #" + attemptId + ")");
            result.setPersistenceError(null);
            return true;

        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }

            result.setPersisted(false);
            result.setPersistenceError(ex.getMessage());
            result.setPersistenceMessage("Database offline: " + ex.getMessage());
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public List<QuizAttempt> loadAllAttempts(int limit) throws SQLException {
        return quizAttemptRepository.findAll(limit);
    }

    public List<QuizAttempt> loadAttemptsByCategory(Category category, int limit) throws SQLException {
        return quizAttemptRepository.findByCategory(category, limit);
    }

    public List<QuizAttempt> loadAttemptsByPlayer(String playerName, int limit) throws SQLException {
        return quizAttemptRepository.findByPlayer(playerName, limit);
    }

    public List<QuizResult.ReviewItem> loadHistoricalReviewItems(int attemptId) throws SQLException {
        return quizAnswerRepository.findReviewItemsByAttemptId(attemptId);
    }

    public DatabaseConnection.ConnectionStatus checkDatabaseStatus() {
        return databaseConnection.testConnection();
    }

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    public PlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public QuizAttemptRepository getQuizAttemptRepository() {
        return quizAttemptRepository;
    }

    public QuizAnswerRepository getQuizAnswerRepository() {
        return quizAnswerRepository;
    }
}

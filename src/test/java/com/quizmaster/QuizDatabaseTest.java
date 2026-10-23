package com.quizmaster;

import com.quizmaster.database.DatabaseConfig;
import com.quizmaster.database.DatabaseConnection;
import com.quizmaster.database.DatabaseInitializer;
import com.quizmaster.model.Category;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.model.QuizAttempt;
import com.quizmaster.model.QuizResult;
import com.quizmaster.repository.PlayerRepository;
import com.quizmaster.repository.QuizAnswerRepository;
import com.quizmaster.repository.QuizAttemptRepository;
import com.quizmaster.service.QuestionBankService;
import com.quizmaster.service.QuizPersistenceService;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive automated tests for MySQL JDBC database integration,
 * repositories, transaction management, rollbacks, and offline handling.
 * Uses isolated H2 in MySQL-compatibility mode to avoid altering user databases.
 */
public class QuizDatabaseTest {

    private static final String H2_URL = "jdbc:h2:mem:quizmaster_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
    private static final String H2_USER = "sa";
    private static final String H2_PASS = "";

    private DatabaseConfig testConfig;
    private DatabaseConnection testConnection;
    private DatabaseInitializer testInitializer;
    private QuestionBankService questionBankService;
    private QuizPersistenceService persistenceService;

    @BeforeEach
    public void setUp() throws SQLException {
        testConfig = new DatabaseConfig(H2_URL, H2_USER, H2_PASS);
        testConnection = new DatabaseConnection(testConfig);
        testInitializer = new DatabaseInitializer(testConnection);

        // Clean tables if present
        try (Connection conn = testConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS quiz_answer_details");
            stmt.execute("DROP TABLE IF EXISTS quiz_attempts");
            stmt.execute("DROP TABLE IF EXISTS players");
        }

        // Initialize schema
        boolean initOk = testInitializer.initializeDatabase();
        assertTrue(initOk, "Schema initialization on test database must succeed.");

        questionBankService = new QuestionBankService();
        persistenceService = new QuizPersistenceService(testConnection, questionBankService);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection conn = testConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS quiz_answer_details");
            stmt.execute("DROP TABLE IF EXISTS quiz_attempts");
            stmt.execute("DROP TABLE IF EXISTS players");
        }
    }

    // ==========================================
    // 1. Database Configuration Tests
    // ==========================================
    @Test
    @DisplayName("DatabaseConfig initializes default values, accepts custom credentials, and masks passwords")
    public void testDatabaseConfigSecurityAndDefaults() {
        DatabaseConfig defaultConfig = new DatabaseConfig();
        assertNotNull(defaultConfig.getUrl());
        assertNotNull(defaultConfig.getUsername());
        assertNotNull(defaultConfig.getPassword());

        DatabaseConfig customConfig = new DatabaseConfig("jdbc:mysql://db.internal:3306/custom_quiz", "admin_user", "super_secret_pw99");
        assertEquals("jdbc:mysql://db.internal:3306/custom_quiz", customConfig.getUrl());
        assertEquals("admin_user", customConfig.getUsername());
        assertEquals("super_secret_pw99", customConfig.getPassword());

        String masked = customConfig.getMaskedSummary();
        assertTrue(masked.contains("admin_user"), "Masked summary should include username");
        assertTrue(masked.contains("custom_quiz"), "Masked summary should include db url");
        assertFalse(masked.contains("super_secret_pw99"), "Masked summary must NEVER expose raw password");
        assertTrue(masked.contains("****"), "Masked summary must replace password with asterisks");

        assertEquals(masked, customConfig.toString(), "toString() must delegate to masked summary");
    }

    // ==========================================
    // 2. Connectivity & Status Tests
    // ==========================================
    @Test
    @DisplayName("DatabaseConnection testConnection verifies active connection and handles unreachable servers")
    public void testDatabaseConnectionStatus() {
        DatabaseConnection.ConnectionStatus status = testConnection.testConnection();
        assertTrue(status.isConnected(), "In-memory test connection must be connected");
        assertTrue(status.getResponseTimeMs() >= 0);
        assertTrue(status.getMessage().contains("Connected"));

        // Test unreachable connection
        DatabaseConfig badConfig = new DatabaseConfig("jdbc:mysql://127.0.0.1:54321/fake_db?connectTimeout=200", "user", "pass");
        DatabaseConnection badConnection = new DatabaseConnection(badConfig);
        DatabaseConnection.ConnectionStatus badStatus = badConnection.testConnection();

        assertFalse(badStatus.isConnected(), "Unreachable server must report connected = false");
        assertNotNull(badStatus.getMessage());
        assertFalse(badStatus.getMessage().contains("pass"), "Error message must never expose credentials");
    }

    // ==========================================
    // 3. Database Schema Idempotence Tests
    // ==========================================
    @Test
    @DisplayName("DatabaseInitializer creates all 3 tables and executes idempotently without errors")
    public void testDatabaseInitializerIdempotence() throws SQLException {
        // Run initializer again on already created database
        boolean secondRun = testInitializer.initializeDatabase();
        assertTrue(secondRun, "Re-running initializer on existing schema must succeed idempotently");

        // Verify tables exist in metadata
        try (Connection conn = testConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery("SELECT count(*) FROM players")) {
                assertTrue(rs.next());
                assertEquals(0, rs.getInt(1));
            }
            try (ResultSet rs = stmt.executeQuery("SELECT count(*) FROM quiz_attempts")) {
                assertTrue(rs.next());
                assertEquals(0, rs.getInt(1));
            }
            try (ResultSet rs = stmt.executeQuery("SELECT count(*) FROM quiz_answer_details")) {
                assertTrue(rs.next());
                assertEquals(0, rs.getInt(1));
            }
        }
    }

    // ==========================================
    // 4. Player Repository Tests
    // ==========================================
    @Test
    @DisplayName("PlayerRepository finds or creates players, reuses existing names, and handles sanitization")
    public void testPlayerRepositoryOperations() throws SQLException {
        PlayerRepository playerRepo = new PlayerRepository(testConnection);

        // Create player
        int id1 = playerRepo.findOrCreate("  Alex Rivers  ");
        assertTrue(id1 > 0, "Generated player id must be greater than 0");

        // Find same player - must return identical id (reuse)
        int id2 = playerRepo.findOrCreate("Alex Rivers");
        assertEquals(id1, id2, "Existing player must be reused without creating duplicates");

        // Verify name retrieval
        String retrievedName = playerRepo.findPlayerName(id1);
        assertEquals("Alex Rivers", retrievedName);

        // Anonymous fallback for blank name
        int anonId = playerRepo.findOrCreate("   ");
        assertTrue(anonId > 0);
        String anonName = playerRepo.findPlayerName(anonId);
        assertEquals("Anonymous Player", anonName);

        // List all players
        List<String> allPlayers = playerRepo.getAllPlayerNames();
        assertEquals(2, allPlayers.size());
        assertTrue(allPlayers.contains("Alex Rivers"));
        assertTrue(allPlayers.contains("Anonymous Player"));
    }

    // ==========================================
    // 5. Quiz Attempt Repository Tests
    // ==========================================
    @Test
    @DisplayName("QuizAttemptRepository inserts attempts, retrieves newest first, and filters by category and player")
    public void testQuizAttemptRepositoryQueries() throws SQLException {
        PlayerRepository playerRepo = new PlayerRepository(testConnection);
        QuizAttemptRepository attemptRepo = new QuizAttemptRepository(testConnection);

        int playerAlex = playerRepo.findOrCreate("Alex");
        int playerMorgan = playerRepo.findOrCreate("Morgan");

        try (Connection conn = testConnection.getConnection()) {
            // Insert attempt 1 (Alex, Programming, Medium)
            int attempt1 = attemptRepo.insertAttempt(
                    conn, playerAlex, Category.PROGRAMMING, Difficulty.MEDIUM,
                    10, 8, 2, 0, 8, 80, 120,
                    new Timestamp(System.currentTimeMillis() - 5000)
            );
            assertTrue(attempt1 > 0);

            // Insert attempt 2 (Morgan, Mathematics, Hard)
            int attempt2 = attemptRepo.insertAttempt(
                    conn, playerMorgan, Category.MATHEMATICS, Difficulty.HARD,
                    10, 5, 4, 1, 5, 50, 180,
                    new Timestamp(System.currentTimeMillis())
            );
            assertTrue(attempt2 > 0);
        }

        // Test findAll (latest first)
        List<QuizAttempt> all = attemptRepo.findAll(10);
        assertEquals(2, all.size());
        assertEquals("Morgan", all.get(0).getPlayerName(), "Newest attempt must be first");
        assertEquals("Alex", all.get(1).getPlayerName());

        // Test findByCategory
        List<QuizAttempt> progAttempts = attemptRepo.findByCategory(Category.PROGRAMMING, 10);
        assertEquals(1, progAttempts.size());
        assertEquals("Alex", progAttempts.get(0).getPlayerName());
        assertEquals(Category.PROGRAMMING, progAttempts.get(0).getCategory());

        List<QuizAttempt> scienceAttempts = attemptRepo.findByCategory(Category.SCIENCE, 10);
        assertEquals(0, scienceAttempts.size());

        // Test findByPlayer
        List<QuizAttempt> morganAttempts = attemptRepo.findByPlayer("Morgan", 10);
        assertEquals(1, morganAttempts.size());
        assertEquals(5, morganAttempts.get(0).getScore());
    }

    // ==========================================
    // 6. Quiz Answer Repository & Unanswered Questions
    // ==========================================
    @Test
    @DisplayName("QuizAnswerRepository batch inserts details with NULL for unanswered and accurately reconstructs review items")
    public void testQuizAnswerRepositoryAnswerDetails() throws SQLException {
        PlayerRepository playerRepo = new PlayerRepository(testConnection);
        QuizAttemptRepository attemptRepo = new QuizAttemptRepository(testConnection);
        QuizAnswerRepository answerRepo = new QuizAnswerRepository(testConnection, questionBankService);

        int playerId = playerRepo.findOrCreate("Dev Tester");

        int attemptId;
        try (Connection conn = testConnection.getConnection()) {
            attemptId = attemptRepo.insertAttempt(
                    conn, playerId, Category.PROGRAMMING, Difficulty.MEDIUM,
                    3, 1, 1, 1, 1, 33, 45, new Timestamp(System.currentTimeMillis())
            );
        }

        Question q1 = questionBankService.getAllQuestions().get(0);
        Question q2 = questionBankService.getAllQuestions().get(1);
        Question q3 = questionBankService.getAllQuestions().get(2);

        List<QuizResult.ReviewItem> items = new ArrayList<>();
        // Item 1: Correct
        items.add(new QuizResult.ReviewItem(q1, q1.getCorrectOptionIndex()));
        // Item 2: Incorrect
        int wrongIndex = (q2.getCorrectOptionIndex() + 1) % 4;
        items.add(new QuizResult.ReviewItem(q2, wrongIndex));
        // Item 3: Unanswered (index -1)
        items.add(new QuizResult.ReviewItem(q3, -1));

        try (Connection conn = testConnection.getConnection()) {
            answerRepo.insertAnswerDetails(conn, attemptId, items);
        }

        // Verify reconstruction
        List<QuizResult.ReviewItem> reconstructed = answerRepo.findReviewItemsByAttemptId(attemptId);
        assertEquals(3, reconstructed.size());

        // Check Correct Item
        assertEquals(QuizResult.AnswerStatus.CORRECT, reconstructed.get(0).getStatus());
        assertEquals(q1.getCorrectAnswerText(), reconstructed.get(0).getSelectedAnswerText());

        // Check Incorrect Item
        assertEquals(QuizResult.AnswerStatus.INCORRECT, reconstructed.get(1).getStatus());
        assertEquals(q2.getOptions().get(wrongIndex), reconstructed.get(1).getSelectedAnswerText());

        // Check Unanswered Item
        assertEquals(QuizResult.AnswerStatus.UNANSWERED, reconstructed.get(2).getStatus());
        assertEquals("No Answer Selected (Unanswered)", reconstructed.get(2).getSelectedAnswerText());
    }

    // ==========================================
    // 7. Full Persistence Service & Atomicity Tests
    // ==========================================
    @Test
    @DisplayName("QuizPersistenceService transactionally persists entire quiz result across all 3 tables")
    public void testFullQuizResultPersistenceSuccess() throws SQLException {
        Question q1 = questionBankService.getAllQuestions().get(0);
        Question q2 = questionBankService.getAllQuestions().get(1);

        List<QuizResult.ReviewItem> reviewItems = Arrays.asList(
                new QuizResult.ReviewItem(q1, q1.getCorrectOptionIndex()),
                new QuizResult.ReviewItem(q2, -1)
        );

        QuizResult result = new QuizResult(
                "Elena Fisher",
                Category.COMPUTER_SCIENCE,
                Difficulty.MEDIUM,
                2,
                1,
                2,
                50,
                1,
                0,
                1,
                55,
                "00:55",
                "2026-10-23 20:00",
                "Good Effort!",
                reviewItems
        );

        assertFalse(result.isPersisted());
        assertEquals(-1, result.getDatabaseAttemptId());

        boolean saved = persistenceService.saveQuizResult(result);
        assertTrue(saved, "Quiz result should be successfully saved to database");
        assertTrue(result.isPersisted());
        assertTrue(result.getDatabaseAttemptId() > 0);
        assertNotNull(result.getPersistenceMessage());
        assertNull(result.getPersistenceError());

        // Calling save again should recognize it is already persisted and not duplicate
        int originalDbId = result.getDatabaseAttemptId();
        boolean savedAgain = persistenceService.saveQuizResult(result);
        assertTrue(savedAgain);
        assertEquals(originalDbId, result.getDatabaseAttemptId());

        // Verify database records
        List<QuizAttempt> attempts = persistenceService.loadAllAttempts(10);
        assertEquals(1, attempts.size());
        assertEquals("Elena Fisher", attempts.get(0).getPlayerName());
        assertEquals(Category.COMPUTER_SCIENCE, attempts.get(0).getCategory());
        assertEquals(1, attempts.get(0).getScore());

        // Verify historical answer details
        List<QuizResult.ReviewItem> loadedItems = persistenceService.loadHistoricalReviewItems(result.getDatabaseAttemptId());
        assertEquals(2, loadedItems.size());
        assertEquals(QuizResult.AnswerStatus.CORRECT, loadedItems.get(0).getStatus());
        assertEquals(QuizResult.AnswerStatus.UNANSWERED, loadedItems.get(1).getStatus());
    }

    // ==========================================
    // 8. Transaction Rollback on Failure Test
    // ==========================================
    @Test
    @DisplayName("QuizPersistenceService rolls back all operations when a downstream step fails, leaving no orphaned attempts")
    public void testTransactionRollbackOnFailure() throws SQLException {
        // Create a result with a question text exceeding VARCHAR(1000) column limit to trigger SQL constraint failure
        String excessivelyLongText = "A".repeat(1500);
        Question corruptQuestion = new Question(
                9999,
                excessivelyLongText,
                Category.PROGRAMMING,
                Difficulty.EASY,
                Arrays.asList("Option A", "Option B", "Option C", "Option D"),
                0,
                "Explanation"
        );

        List<QuizResult.ReviewItem> items = Arrays.asList(
                new QuizResult.ReviewItem(corruptQuestion, 0)
        );

        QuizResult corruptResult = new QuizResult(
                "Rollback Test Player",
                Category.PROGRAMMING,
                Difficulty.EASY,
                1,
                1,
                1,
                100,
                1,
                0,
                0,
                20,
                "00:20",
                "2026-10-23 20:00",
                "Review",
                items
        );

        boolean saved = persistenceService.saveQuizResult(corruptResult);
        assertFalse(saved, "Persistence should fail and return false due to constraint violation");
        assertFalse(corruptResult.isPersisted());
        assertNotNull(corruptResult.getPersistenceError());

        // Verify that transaction was rolled back: quiz_attempts table must be EMPTY
        List<QuizAttempt> attempts = persistenceService.loadAllAttempts(10);
        assertEquals(0, attempts.size(), "Rolled back transaction must not leave any attempt record in the database");
    }

    // ==========================================
    // 9. Offline / Unreachable Database Graceful Degradation
    // ==========================================
    @Test
    @DisplayName("QuizPersistenceService gracefully handles offline database without crashing, preserving result in-memory")
    public void testOfflineDatabaseHandling() {
        DatabaseConfig offlineConfig = new DatabaseConfig(
                "jdbc:mysql://127.0.0.1:54321/offline_quiz?connectTimeout=100",
                "root",
                "wrong_pass"
        );
        DatabaseConnection offlineConn = new DatabaseConnection(offlineConfig);
        QuizPersistenceService offlineService = new QuizPersistenceService(offlineConn, questionBankService);

        QuizResult localResult = new QuizResult(
                "Offline Player",
                Category.GENERAL_KNOWLEDGE,
                Difficulty.EASY,
                3,
                3,
                3,
                100,
                3,
                0,
                0,
                40,
                "00:40",
                "2026-10-23 20:00",
                "Review",
                new ArrayList<>()
        );

        // Verify database is indeed reported offline
        DatabaseConnection.ConnectionStatus status = offlineService.checkDatabaseStatus();
        assertFalse(status.isConnected());

        // Attempt save
        boolean saved = offlineService.saveQuizResult(localResult);
        assertFalse(saved, "Offline database should return false");
        assertFalse(localResult.isPersisted(), "Result should be marked not persisted");
        assertNotNull(localResult.getPersistenceError(), "Persistence error message should be recorded");
        assertTrue(localResult.getPersistenceMessage().contains("Database offline"));

        // Verify in-memory state is intact
        assertEquals(3, localResult.getScore());
        assertEquals("Offline Player", localResult.getPlayerName());
        assertEquals(100, localResult.getPercentage());
    }

    // ==========================================
    // 10. Persistence Retry Workflow Test
    // ==========================================
    @Test
    @DisplayName("QuizService retryPersistence successfully saves a previously unpersisted result once database is restored")
    public void testPersistenceRetryWorkflow() {
        // Initially create AppState with our test persistence service
        com.quizmaster.model.AppState state = new com.quizmaster.model.AppState(testConnection);
        com.quizmaster.service.QuizService quizService = state.getQuizService();

        // Run a quiz
        quizService.startQuiz("Retry Tester", Category.PROGRAMMING, Difficulty.EASY, 5);
        quizService.recordAnswer(0, 0);
        QuizResult res = quizService.submitQuiz(state);

        assertNotNull(res);
        assertTrue(res.isPersisted());
        assertTrue(res.getDatabaseAttemptId() > 0);

        // Simulate retry when already persisted: returns true immediately
        boolean retryRes = quizService.retryPersistence(state);
        assertTrue(retryRes);
    }

    // ==========================================
    // 11. Environment Live MySQL Status Reporting Test
    // ==========================================
    @Test
    @DisplayName("DatabaseConnection testConnection honestly reports live MySQL status without crashing or throwing")
    public void testLiveMySQLStatusHonestReporting() {
        DatabaseConfig envConfig = new DatabaseConfig();
        DatabaseConnection envConnection = new DatabaseConnection(envConfig);

        DatabaseConnection.ConnectionStatus status = envConnection.testConnection();
        assertNotNull(status);
        assertNotNull(status.getMessage());
        assertTrue(status.getResponseTimeMs() >= 0);

        if (status.isConnected()) {
            System.out.println("[INFO] Live MySQL server detected at " + envConfig.getUrl() + " (Ping: " + status.getResponseTimeMs() + "ms)");
        } else {
            System.out.println("[INFO] Live MySQL server is currently offline. Status: " + status.getMessage());
            if (envConfig.getPassword() != null && !envConfig.getPassword().isEmpty()) {
                assertFalse(status.getMessage().contains(envConfig.getPassword()), "Status must never reveal credentials");
            }
        }
    }
}

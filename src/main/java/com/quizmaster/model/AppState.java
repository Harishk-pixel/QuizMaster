package com.quizmaster.model;

import com.quizmaster.database.DatabaseConnection;
import com.quizmaster.database.DatabaseInitializer;
import com.quizmaster.service.QuestionBankService;
import com.quizmaster.service.QuizPersistenceService;
import com.quizmaster.service.QuizService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central in-memory state for QuizMaster during Phase 3.
 * Preserves player preferences, active session services, database persistence,
 * demo records, and historical quiz attempts.
 */
public class AppState {
    private String playerName = "";
    private Category selectedCategory = Category.PROGRAMMING;
    private Difficulty selectedDifficulty = Difficulty.MEDIUM;
    private int selectedQuestionCount = 5;

    // Services
    private final QuestionBankService questionBankService;
    private final QuizPersistenceService persistenceService;
    private final QuizService quizService;

    // Historical attempts and active results
    private final List<QuizAttempt> scoreHistory = new ArrayList<>();
    private QuizResult currentResult;

    // Review session targeting historical attempts
    private QuizAttempt selectedHistoricalAttempt;

    public AppState() {
        this(new DatabaseConnection());
    }

    public AppState(DatabaseConnection databaseConnection) {
        this.questionBankService = new QuestionBankService();
        this.persistenceService = new QuizPersistenceService(databaseConnection, this.questionBankService);
        this.quizService = new QuizService(this.questionBankService, this.persistenceService);

        // Attempt non-destructive database schema initialization
        try {
            DatabaseInitializer initializer = new DatabaseInitializer(databaseConnection);
            initializer.initializeDatabase();
        } catch (Exception ignored) {
        }

        initScoreHistory();
    }

    public void initScoreHistory() {
        // First try to load persistent attempts from MySQL
        boolean loaded = refreshHistoryFromDatabase();

        // If no records in database or database is not reachable, populate baseline demo history
        if (!loaded || scoreHistory.isEmpty()) {
            scoreHistory.clear();
            scoreHistory.add(new QuizAttempt(1084, "QM-1084", "Alex Hunter", "2026-10-22 18:45", Category.PROGRAMMING, Difficulty.MEDIUM, 8, 10, 80, 8, 2, 0, 180));
            scoreHistory.add(new QuizAttempt(1083, "QM-1083", "Alex Hunter", "2026-10-21 14:15", Category.COMPUTER_SCIENCE, Difficulty.HARD, 7, 10, 70, 7, 3, 0, 240));
            scoreHistory.add(new QuizAttempt(1082, "QM-1082", "Sarah Connor", "2026-10-20 11:30", Category.SCIENCE, Difficulty.MEDIUM, 9, 10, 90, 9, 1, 0, 150));
            scoreHistory.add(new QuizAttempt(1081, "QM-1081", "Neo Anderson", "2026-10-19 16:20", Category.GENERAL_KNOWLEDGE, Difficulty.EASY, 10, 10, 100, 10, 0, 0, 110));
            scoreHistory.add(new QuizAttempt(1080, "QM-1080", "Ada Lovelace", "2026-10-18 09:10", Category.MATHEMATICS, Difficulty.HARD, 6, 10, 60, 6, 4, 0, 300));
        }

        // Create default demo result
        generateDemoResult();
    }

    public boolean refreshHistoryFromDatabase() {
        try {
            List<QuizAttempt> dbAttempts = persistenceService.loadAllAttempts(100);
            if (dbAttempts != null && !dbAttempts.isEmpty()) {
                scoreHistory.clear();
                scoreHistory.addAll(dbAttempts);
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public void generateDemoResult() {
        String name = (playerName != null && !playerName.trim().isEmpty()) ? playerName : "Alex Hunter";
        List<Question> questions = questionBankService.getQuestions(selectedCategory, selectedDifficulty, 5);
        if (questions.isEmpty()) {
            questions = questionBankService.getQuestions(Category.PROGRAMMING, Difficulty.MEDIUM, 5);
        }

        Map<Integer, Integer> answers = new HashMap<>();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int chosen = (i == 2) ? (q.getCorrectOptionIndex() + 1) % 4 : q.getCorrectOptionIndex();
            answers.put(i, chosen);
        }

        this.currentResult = quizService.getScoreCalculator().calculateResult(
                name,
                selectedCategory,
                selectedDifficulty,
                questions,
                answers,
                75
        );
    }

    // Getters and Setters
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public Difficulty getSelectedDifficulty() {
        return selectedDifficulty;
    }

    public void setSelectedDifficulty(Difficulty selectedDifficulty) {
        this.selectedDifficulty = selectedDifficulty;
    }

    public int getSelectedQuestionCount() {
        return selectedQuestionCount;
    }

    public void setSelectedQuestionCount(int selectedQuestionCount) {
        this.selectedQuestionCount = selectedQuestionCount;
    }

    public QuestionBankService getQuestionBankService() {
        return questionBankService;
    }

    public QuizPersistenceService getPersistenceService() {
        return persistenceService;
    }

    public QuizService getQuizService() {
        return quizService;
    }

    public List<QuizAttempt> getScoreHistory() {
        return scoreHistory;
    }

    public QuizResult getCurrentResult() {
        return currentResult;
    }

    public void setCurrentResult(QuizResult currentResult) {
        this.currentResult = currentResult;
    }

    public QuizAttempt getSelectedHistoricalAttempt() {
        return selectedHistoricalAttempt;
    }

    public void setSelectedHistoricalAttempt(QuizAttempt selectedHistoricalAttempt) {
        this.selectedHistoricalAttempt = selectedHistoricalAttempt;
    }

    public List<Question> getDemoQuestions() {
        return questionBankService.getQuestions(selectedCategory, selectedDifficulty, 5);
    }
}

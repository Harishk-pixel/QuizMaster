package com.quizmaster.service;

import com.quizmaster.database.DatabaseConnection;
import com.quizmaster.model.AppState;
import com.quizmaster.model.Category;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.model.QuizAttempt;
import com.quizmaster.model.QuizResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestrating service managing active quiz sessions, question navigation,
 * answer recordings, timer integration, transactional database persistence,
 * and atomic score submission.
 */
public class QuizService {

    private final QuestionBankService questionBankService;
    private final QuizTimerService timerService;
    private final ScoreCalculator scoreCalculator;
    private final QuizPersistenceService persistenceService;

    private String activePlayerName = "";
    private Category activeCategory = Category.PROGRAMMING;
    private Difficulty activeDifficulty = Difficulty.MEDIUM;

    private List<Question> activeQuestions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private final Map<Integer, Integer> userAnswers = new HashMap<>();

    private boolean submitted = false;
    private QuizResult lastResult;

    public QuizService(QuestionBankService questionBankService) {
        this(questionBankService, new QuizPersistenceService(new DatabaseConnection(), questionBankService));
    }

    public QuizService(QuestionBankService questionBankService, QuizPersistenceService persistenceService) {
        this.questionBankService = questionBankService != null ? questionBankService : new QuestionBankService();
        this.timerService = new QuizTimerService();
        this.scoreCalculator = new ScoreCalculator();
        this.persistenceService = persistenceService;
    }

    public synchronized void startQuiz(String playerName, Category category, Difficulty difficulty, int questionCount) {
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be empty.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null.");
        }
        if (questionCount <= 0) {
            throw new IllegalArgumentException("Question count must be positive.");
        }

        // Fetch questions from bank
        List<Question> questions = questionBankService.getQuestions(category, difficulty, questionCount);
        if (questions.size() < questionCount) {
            throw new IllegalStateException("Insufficient questions available: requested " + questionCount
                    + " but only " + questions.size() + " are available for " + category.getDisplayName()
                    + " (" + difficulty.getDisplayName() + ").");
        }

        // Reset session state
        this.activePlayerName = playerName.trim();
        this.activeCategory = category;
        this.activeDifficulty = difficulty;
        this.activeQuestions = new ArrayList<>(questions);
        this.currentQuestionIndex = 0;
        this.userAnswers.clear();
        this.submitted = false;
        this.lastResult = null;

        // Start countdown timer: 30 seconds per question
        int totalSeconds = questionCount * QuizTimerService.DEFAULT_SECONDS_PER_QUESTION;
        timerService.start(totalSeconds);
    }

    public synchronized void recordAnswer(int questionIndex, int optionIndex) {
        if (submitted) return;
        if (questionIndex >= 0 && questionIndex < activeQuestions.size()) {
            userAnswers.put(questionIndex, optionIndex);
        }
    }

    public synchronized Integer getUserAnswer(int questionIndex) {
        return userAnswers.get(questionIndex);
    }

    public synchronized boolean isQuestionAnswered(int questionIndex) {
        Integer ans = userAnswers.get(questionIndex);
        return ans != null && ans >= 0;
    }

    public synchronized boolean isCurrentQuestionAnswered() {
        return isQuestionAnswered(currentQuestionIndex);
    }

    public synchronized Question getCurrentQuestion() {
        if (activeQuestions.isEmpty() || currentQuestionIndex < 0 || currentQuestionIndex >= activeQuestions.size()) {
            return null;
        }
        return activeQuestions.get(currentQuestionIndex);
    }

    public synchronized boolean hasNextQuestion() {
        return currentQuestionIndex < activeQuestions.size() - 1;
    }

    public synchronized boolean hasPreviousQuestion() {
        return currentQuestionIndex > 0;
    }

    public synchronized boolean nextQuestion() {
        if (hasNextQuestion()) {
            currentQuestionIndex++;
            return true;
        }
        return false;
    }

    public synchronized boolean previousQuestion() {
        if (hasPreviousQuestion()) {
            currentQuestionIndex--;
            return true;
        }
        return false;
    }

    public synchronized void goToQuestion(int index) {
        if (index >= 0 && index < activeQuestions.size()) {
            this.currentQuestionIndex = index;
        }
    }

    /**
     * Atomically submits the active quiz, stops the timer, computes score,
     * transactionally persists the result into MySQL if available, and updates AppState.
     * Prevents accidental duplicate submissions.
     */
    public synchronized QuizResult submitQuiz(AppState appState) {
        if (submitted && lastResult != null) {
            return lastResult;
        }

        // Stop timer immediately
        int elapsedSeconds = timerService.getElapsedSeconds();
        timerService.stop();

        // Calculate score result
        this.lastResult = scoreCalculator.calculateResult(
                activePlayerName,
                activeCategory,
                activeDifficulty,
                activeQuestions,
                userAnswers,
                elapsedSeconds
        );
        this.submitted = true;

        // Transactionally persist to MySQL database
        if (persistenceService != null) {
            persistenceService.saveQuizResult(lastResult);
        }

        // Propagate result and attempt history to AppState
        if (appState != null) {
            appState.setCurrentResult(lastResult);

            // Record as attempt in score history
            int dbId = lastResult.getDatabaseAttemptId();
            String attemptId = (dbId > 0) ? ("QM-" + dbId) : ("QM-" + (1085 + appState.getScoreHistory().size()));
            QuizAttempt attempt = new QuizAttempt(
                    dbId,
                    attemptId,
                    activePlayerName,
                    lastResult.getCompletedAt(),
                    activeCategory,
                    activeDifficulty,
                    lastResult.getScore(),
                    lastResult.getMaxScore(),
                    lastResult.getPercentage(),
                    lastResult.getCorrectCount(),
                    lastResult.getIncorrectCount(),
                    lastResult.getUnansweredCount(),
                    elapsedSeconds
            );
            appState.getScoreHistory().add(0, attempt); // Insert at top
        }

        return lastResult;
    }

    public synchronized boolean retryPersistence(AppState appState) {
        if (lastResult == null || lastResult.isPersisted()) {
            return true;
        }
        if (persistenceService == null) {
            return false;
        }

        boolean saved = persistenceService.saveQuizResult(lastResult);
        if (saved && appState != null && !appState.getScoreHistory().isEmpty()) {
            QuizAttempt top = appState.getScoreHistory().get(0);
            int dbId = lastResult.getDatabaseAttemptId();
            QuizAttempt updated = new QuizAttempt(
                    dbId,
                    "QM-" + dbId,
                    top.getPlayerName(),
                    top.getDate(),
                    top.getCategory(),
                    top.getDifficulty(),
                    top.getScore(),
                    top.getMaxScore(),
                    top.getPercentage(),
                    top.getCorrectAnswers(),
                    top.getIncorrectAnswers(),
                    top.getUnanswered(),
                    top.getTimeTakenSeconds()
            );
            appState.getScoreHistory().set(0, updated);
        }
        return saved;
    }

    public synchronized void cancelQuiz() {
        timerService.stop();
        submitted = true;
    }

    // Getters
    public synchronized int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public synchronized int getTotalQuestions() {
        return activeQuestions.size();
    }

    public synchronized List<Question> getActiveQuestions() {
        return Collections.unmodifiableList(activeQuestions);
    }

    public synchronized Map<Integer, Integer> getUserAnswers() {
        return Collections.unmodifiableMap(userAnswers);
    }

    public synchronized int getAnsweredCount() {
        int count = 0;
        for (int i = 0; i < activeQuestions.size(); i++) {
            if (isQuestionAnswered(i)) count++;
        }
        return count;
    }

    public synchronized int getUnansweredCount() {
        return activeQuestions.size() - getAnsweredCount();
    }

    public synchronized boolean isSubmitted() {
        return submitted;
    }

    public synchronized QuizResult getLastResult() {
        return lastResult;
    }

    public QuizTimerService getTimerService() {
        return timerService;
    }

    public QuestionBankService getQuestionBankService() {
        return questionBankService;
    }

    public ScoreCalculator getScoreCalculator() {
        return scoreCalculator;
    }

    public QuizPersistenceService getPersistenceService() {
        return persistenceService;
    }

    public String getActivePlayerName() {
        return activePlayerName;
    }

    public Category getActiveCategory() {
        return activeCategory;
    }

    public Difficulty getActiveDifficulty() {
        return activeDifficulty;
    }
}

package com.quizmaster.model;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the results of a quiz session, including per-question answers for review,
 * calculated metrics, elapsed duration, and performance appraisal.
 */
public class QuizResult {
    public enum AnswerStatus {
        CORRECT,
        INCORRECT,
        UNANSWERED
    }

    private final String playerName;
    private final Category category;
    private final Difficulty difficulty;
    private final int totalQuestions;
    private final int score;
    private final int maxScore;
    private final int percentage;
    private final int correctCount;
    private final int incorrectCount;
    private final int unansweredCount;
    private final int timeTakenSeconds;
    private final String timeTakenFormatted;
    private final String completedAt;
    private final String performanceMessage;
    private final List<ReviewItem> reviewItems;

    // Database persistence tracking
    private boolean persisted = false;
    private int databaseAttemptId = -1;
    private String persistenceMessage = "Not saved to database";
    private String persistenceError = null;

    public QuizResult(String playerName, Category category, Difficulty difficulty,
                      int totalQuestions, int score, int maxScore, int percentage,
                      int correctCount, int incorrectCount, int unansweredCount,
                      int timeTakenSeconds, String timeTakenFormatted,
                      String completedAt, String performanceMessage,
                      List<ReviewItem> reviewItems) {
        this.playerName = playerName != null && !playerName.trim().isEmpty() ? playerName.trim() : "Anonymous Player";
        this.category = category;
        this.difficulty = difficulty;
        this.totalQuestions = totalQuestions;
        this.score = score;
        this.maxScore = maxScore;
        this.percentage = percentage;
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.unansweredCount = unansweredCount;
        this.timeTakenSeconds = timeTakenSeconds;
        this.timeTakenFormatted = timeTakenFormatted != null ? timeTakenFormatted : "00:00";
        this.completedAt = completedAt != null ? completedAt : "";
        this.performanceMessage = performanceMessage != null ? performanceMessage : "";
        this.reviewItems = reviewItems != null ? Collections.unmodifiableList(reviewItems) : Collections.emptyList();
    }

    // Overload constructor for backwards compatibility
    public QuizResult(String playerName, Category category, Difficulty difficulty,
                      int score, int maxScore, int percentage,
                      int correctCount, int incorrectCount, int unansweredCount,
                      String completedAt, List<ReviewItem> reviewItems) {
        this(playerName, category, difficulty,
             maxScore, score, maxScore, percentage,
             correctCount, incorrectCount, unansweredCount,
             0, "00:00", completedAt, "Good Effort!", reviewItems);
    }

    public String getPlayerName() {
        return playerName;
    }

    public Category getCategory() {
        return category;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getPercentage() {
        return percentage;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getIncorrectCount() {
        return incorrectCount;
    }

    public int getUnansweredCount() {
        return unansweredCount;
    }

    public int getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public String getTimeTakenFormatted() {
        return timeTakenFormatted;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public String getPerformanceMessage() {
        return performanceMessage;
    }

    public List<ReviewItem> getReviewItems() {
        return reviewItems;
    }

    public boolean isPersisted() {
        return persisted;
    }

    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }

    public int getDatabaseAttemptId() {
        return databaseAttemptId;
    }

    public void setDatabaseAttemptId(int databaseAttemptId) {
        this.databaseAttemptId = databaseAttemptId;
    }

    public String getPersistenceMessage() {
        return persistenceMessage;
    }

    public void setPersistenceMessage(String persistenceMessage) {
        this.persistenceMessage = persistenceMessage;
    }

    public String getPersistenceError() {
        return persistenceError;
    }

    public void setPersistenceError(String persistenceError) {
        this.persistenceError = persistenceError;
    }

    /**
     * Item representing a question along with player's selected answer, options, and explanation.
     */
    public static class ReviewItem {
        private final Question question;
        private final int selectedOptionIndex; // -1 if unanswered

        public ReviewItem(Question question, int selectedOptionIndex) {
            this.question = question;
            this.selectedOptionIndex = selectedOptionIndex;
        }

        public Question getQuestion() {
            return question;
        }

        public int getSelectedOptionIndex() {
            return selectedOptionIndex;
        }

        public boolean isAnswered() {
            return selectedOptionIndex >= 0;
        }

        public boolean isCorrect() {
            return isAnswered() && selectedOptionIndex == question.getCorrectOptionIndex();
        }

        public AnswerStatus getStatus() {
            if (!isAnswered()) {
                return AnswerStatus.UNANSWERED;
            }
            return isCorrect() ? AnswerStatus.CORRECT : AnswerStatus.INCORRECT;
        }

        public String getSelectedAnswerText() {
            if (selectedOptionIndex >= 0 && selectedOptionIndex < question.getOptions().size()) {
                return question.getOptions().get(selectedOptionIndex);
            }
            return "No Answer Selected (Unanswered)";
        }

        public String getCorrectAnswerText() {
            return question.getCorrectAnswerText();
        }

        public String getExplanation() {
            return question.getExplanation();
        }
    }
}

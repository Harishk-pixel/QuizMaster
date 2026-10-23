package com.quizmaster.model;

/**
 * Represents a historical quiz attempt record used in the score history table and statistics.
 */
public class QuizAttempt {
    private final int dbId;
    private final String attemptId;
    private final String playerName;
    private final String date;
    private final Category category;
    private final Difficulty difficulty;
    private final int score;
    private final int maxScore;
    private final int percentage;
    private final int correctAnswers;
    private final int incorrectAnswers;
    private final int unanswered;
    private final int timeTakenSeconds;

    public QuizAttempt(int dbId, String attemptId, String playerName, String date,
                       Category category, Difficulty difficulty,
                       int score, int maxScore, int percentage,
                       int correctAnswers, int incorrectAnswers, int unanswered,
                       int timeTakenSeconds) {
        this.dbId = dbId;
        this.attemptId = attemptId;
        this.playerName = (playerName != null && !playerName.trim().isEmpty()) ? playerName.trim() : "Anonymous";
        this.date = date;
        this.category = category;
        this.difficulty = difficulty;
        this.score = score;
        this.maxScore = maxScore;
        this.percentage = percentage;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
        this.unanswered = unanswered;
        this.timeTakenSeconds = timeTakenSeconds;
    }

    // Overloaded constructor for backwards compatibility
    public QuizAttempt(String attemptId, String date, Category category, Difficulty difficulty,
                       int score, int maxScore, int percentage,
                       int correctAnswers, int incorrectAnswers, int unanswered) {
        this(-1, attemptId, "Player", date, category, difficulty, score, maxScore, percentage,
             correctAnswers, incorrectAnswers, unanswered, 0);
    }

    public int getDbId() {
        return dbId;
    }

    public String getAttemptId() {
        return attemptId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getDate() {
        return date;
    }

    public Category getCategory() {
        return category;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public String getScoreFormatted() {
        return score + " / " + maxScore;
    }

    public int getPercentage() {
        return percentage;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public int getUnanswered() {
        return unanswered;
    }

    public int getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public String getTimeTakenFormatted() {
        int safe = Math.max(0, timeTakenSeconds);
        int mins = safe / 60;
        int secs = safe % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}

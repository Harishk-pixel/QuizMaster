package com.quizmaster.service;

import com.quizmaster.model.Category;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.model.QuizResult;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Reusable scoring engine evaluating player responses, calculating metrics,
 * and generating detailed quiz review summaries.
 */
public class ScoreCalculator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public QuizResult calculateResult(String playerName, Category category, Difficulty difficulty,
                                      List<Question> questions, Map<Integer, Integer> userAnswers,
                                      int timeTakenSeconds) {
        String safeName = (playerName != null && !playerName.trim().isEmpty()) ? playerName.trim() : "Anonymous Player";
        int total = (questions != null) ? questions.size() : 0;
        int maxScore = total;

        if (total == 0) {
            return new QuizResult(
                    safeName, category, difficulty, 0, 0, 0, 0, 0, 0, 0,
                    timeTakenSeconds, formatTime(timeTakenSeconds),
                    LocalDateTime.now().format(DATE_FORMATTER),
                    "No questions were presented in this session.",
                    Collections.emptyList()
            );
        }

        int correctCount = 0;
        int incorrectCount = 0;
        int unansweredCount = 0;
        List<QuizResult.ReviewItem> reviewItems = new ArrayList<>();

        for (int i = 0; i < total; i++) {
            Question q = questions.get(i);
            Integer selectedOption = (userAnswers != null) ? userAnswers.get(i) : null;

            if (selectedOption == null || selectedOption < 0) {
                unansweredCount++;
                reviewItems.add(new QuizResult.ReviewItem(q, -1));
            } else if (selectedOption == q.getCorrectOptionIndex()) {
                correctCount++;
                reviewItems.add(new QuizResult.ReviewItem(q, selectedOption));
            } else {
                incorrectCount++;
                reviewItems.add(new QuizResult.ReviewItem(q, selectedOption));
            }
        }

        int finalScore = correctCount;
        int percentage = Math.round(((float) correctCount / total) * 100.0f);
        String completedAt = LocalDateTime.now().format(DATE_FORMATTER);
        String message = evaluatePerformanceMessage(percentage);

        return new QuizResult(
                safeName,
                category,
                difficulty,
                total,
                finalScore,
                maxScore,
                percentage,
                correctCount,
                incorrectCount,
                unansweredCount,
                timeTakenSeconds,
                formatTime(timeTakenSeconds),
                completedAt,
                message,
                reviewItems
        );
    }

    private String evaluatePerformanceMessage(int percentage) {
        if (percentage >= 100) {
            return "🏆 Outstanding Mastery! You scored a perfect 100%!";
        } else if (percentage >= 80) {
            return "🌟 Excellent Job! You demonstrated strong conceptual understanding.";
        } else if (percentage >= 60) {
            return "👍 Good Effort! You passed with a solid foundation.";
        } else {
            return "📚 Keep Practicing! Review the explanations below to strengthen key areas.";
        }
    }

    public static String formatTime(int totalSeconds) {
        int safe = Math.max(0, totalSeconds);
        int mins = safe / 60;
        int secs = safe % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}

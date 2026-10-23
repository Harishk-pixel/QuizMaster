package com.quizmaster.repository;

import com.quizmaster.database.DatabaseConnection;
import com.quizmaster.model.Category;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.model.QuizResult;
import com.quizmaster.service.QuestionBankService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data access repository for the quiz_answer_details table.
 * Persists every question snapshot and user response, including unanswered questions.
 */
public class QuizAnswerRepository {

    private final DatabaseConnection databaseConnection;
    private final QuestionBankService questionBankService;

    public QuizAnswerRepository(DatabaseConnection databaseConnection, QuestionBankService questionBankService) {
        this.databaseConnection = databaseConnection != null ? databaseConnection : new DatabaseConnection();
        this.questionBankService = questionBankService != null ? questionBankService : new QuestionBankService();
    }

    /**
     * Inserts all question answer details for an attempt within an active transaction.
     */
    public void insertAnswerDetails(Connection conn, int attemptId, List<QuizResult.ReviewItem> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO quiz_answer_details (" +
                "attempt_id, question_id, question_text, selected_answer, " +
                "correct_answer, is_correct, explanation) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (QuizResult.ReviewItem item : items) {
                stmt.setInt(1, attemptId);
                stmt.setInt(2, item.getQuestion().getId());
                stmt.setString(3, item.getQuestion().getQuestionText());

                if (item.isAnswered()) {
                    stmt.setString(4, item.getSelectedAnswerText());
                } else {
                    stmt.setNull(4, Types.VARCHAR);
                }

                stmt.setString(5, item.getCorrectAnswerText());
                stmt.setBoolean(6, item.isCorrect());
                stmt.setString(7, item.getExplanation());

                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Reconstructs ReviewItems for a completed attempt from the database.
     */
    public List<QuizResult.ReviewItem> findReviewItemsByAttemptId(int attemptId) throws SQLException {
        String sql = "SELECT * FROM quiz_answer_details WHERE attempt_id = ? ORDER BY detail_id ASC";
        List<QuizResult.ReviewItem> reviewItems = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int questionId = rs.getInt("question_id");
                    String qText = rs.getString("question_text");
                    String selectedAnswer = rs.getString("selected_answer");
                    String correctAnswer = rs.getString("correct_answer");
                    String explanation = rs.getString("explanation");

                    // Attempt to locate original Question in bank for full options
                    Question original = findOriginalQuestion(questionId, qText);
                    Question questionToUse;
                    int selectedOptionIndex = -1;

                    if (original != null) {
                        questionToUse = original;
                        if (selectedAnswer != null) {
                            for (int i = 0; i < original.getOptions().size(); i++) {
                                if (original.getOptions().get(i).equalsIgnoreCase(selectedAnswer)) {
                                    selectedOptionIndex = i;
                                    break;
                                }
                            }
                        }
                    } else {
                        // Reconstruct question from snapshot
                        List<String> options = Arrays.asList(
                                correctAnswer,
                                (selectedAnswer != null && !selectedAnswer.equalsIgnoreCase(correctAnswer)) ? selectedAnswer : "Option B",
                                "Option C",
                                "Option D"
                        );
                        questionToUse = new Question(
                                questionId > 0 ? questionId : 1,
                                qText,
                                Category.PROGRAMMING,
                                Difficulty.MEDIUM,
                                options,
                                0,
                                explanation
                        );
                        selectedOptionIndex = (selectedAnswer == null) ? -1 : (selectedAnswer.equalsIgnoreCase(correctAnswer) ? 0 : 1);
                    }

                    reviewItems.add(new QuizResult.ReviewItem(questionToUse, selectedOptionIndex));
                }
            }
        }

        return reviewItems;
    }

    private Question findOriginalQuestion(int id, String text) {
        if (questionBankService != null) {
            for (Question q : questionBankService.getAllQuestions()) {
                if (q.getId() == id || q.getQuestionText().equalsIgnoreCase(text)) {
                    return q;
                }
            }
        }
        return null;
    }
}

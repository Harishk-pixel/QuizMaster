package com.quizmaster.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single multiple-choice question with four distinct options,
 * one correct answer index, and an educational explanation.
 */
public class Question {
    private final int id;
    private final String questionText;
    private final Category category;
    private final Difficulty difficulty;
    private final List<String> options;
    private final int correctOptionIndex;
    private final String explanation;

    public Question(int id, String questionText, Category category, Difficulty difficulty,
                    List<String> options, int correctOptionIndex, String explanation) {
        if (id <= 0) {
            throw new IllegalArgumentException("Question ID must be a positive integer.");
        }
        this.id = id;
        this.questionText = Objects.requireNonNull(questionText, "Question text cannot be null").trim();
        if (this.questionText.isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be blank.");
        }
        this.category = Objects.requireNonNull(category, "Category cannot be null");
        this.difficulty = Objects.requireNonNull(difficulty, "Difficulty cannot be null");
        Objects.requireNonNull(options, "Options list cannot be null");
        if (options.size() != 4) {
            throw new IllegalArgumentException("Question must have exactly 4 answer options (received " + options.size() + ").");
        }
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i) == null || options.get(i).trim().isEmpty()) {
                throw new IllegalArgumentException("Answer option " + (i + 1) + " cannot be blank.");
            }
        }
        this.options = Collections.unmodifiableList(options);
        if (correctOptionIndex < 0 || correctOptionIndex >= options.size()) {
            throw new IllegalArgumentException("Correct option index must be between 0 and 3 (was " + correctOptionIndex + ").");
        }
        this.correctOptionIndex = correctOptionIndex;
        this.explanation = explanation != null ? explanation.trim() : "";
    }

    public int getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public Category getCategory() {
        return category;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public String getCorrectAnswerText() {
        if (correctOptionIndex >= 0 && correctOptionIndex < options.size()) {
            return options.get(correctOptionIndex);
        }
        return "";
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return id == question.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

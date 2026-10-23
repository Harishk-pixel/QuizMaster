package com.quizmaster.model;

import java.awt.Color;

/**
 * Difficulty levels for quizzes with corresponding badges and theme colors.
 */
public enum Difficulty {
    EASY("Easy", new Color(0x35C98A), "Standard pacing and fundamental concepts"),
    MEDIUM("Medium", new Color(0xFFB039), "Balanced challenge requiring analytical thinking"),
    HARD("Hard", new Color(0xFF647C), "Deep technical questions and complex scenarios");

    private final String displayName;
    private final Color badgeColor;
    private final String description;

    Difficulty(String displayName, Color badgeColor, String description) {
        this.displayName = displayName;
        this.badgeColor = badgeColor;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getBadgeColor() {
        return badgeColor;
    }

    public String getDescription() {
        return description;
    }

    public static Difficulty fromDisplayName(String name) {
        for (Difficulty d : values()) {
            if (d.displayName.equalsIgnoreCase(name)) {
                return d;
            }
        }
        return MEDIUM;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

package com.quizmaster.model;

/**
 * Quiz categories available in QuizMaster Phase 2.
 */
public enum Category {
    GENERAL_KNOWLEDGE("General Knowledge", "History, Geography, Inventions & World Trivia", "💡"),
    SCIENCE("Science", "Physics, Chemistry, Biology & Astronomy", "🔬"),
    COMPUTER_SCIENCE("Computer Science", "Algorithms, Data Structures, Operating Systems & Networks", "💻"),
    MATHEMATICS("Mathematics", "Algebra, Arithmetic, Geometry, Probability & Logic", "📐"),
    PROGRAMMING("Programming", "Java, OOP, Syntax, JVM & Software Design", "⌨️");

    private final String displayName;
    private final String description;
    private final String symbol;

    Category(String displayName, String description, String symbol) {
        this.displayName = displayName;
        this.description = description;
        this.symbol = symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Category fromDisplayName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return PROGRAMMING;
        }
        for (Category c : values()) {
            if (c.displayName.equalsIgnoreCase(name) || c.name().equalsIgnoreCase(name)) {
                return c;
            }
        }
        // Aliases for backwards compatibility with Phase 1
        if (name.equalsIgnoreCase("Java Programming")) return PROGRAMMING;
        if (name.equalsIgnoreCase("Data Structures")) return COMPUTER_SCIENCE;
        if (name.equalsIgnoreCase("Computer Networks")) return COMPUTER_SCIENCE;

        return PROGRAMMING;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

-- ==========================================================
-- QuizMaster Database Schema
-- Database: quizmaster_db
-- ==========================================================

-- Table 1: Players
CREATE TABLE IF NOT EXISTS players (
    player_id INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_players_name ON players(player_name);

-- Table 2: Quiz Attempts
CREATE TABLE IF NOT EXISTS quiz_attempts (
    attempt_id INT AUTO_INCREMENT PRIMARY KEY,
    player_id INT NOT NULL,
    category VARCHAR(50) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    total_questions INT NOT NULL,
    correct_answers INT NOT NULL,
    incorrect_answers INT NOT NULL,
    unanswered_questions INT NOT NULL,
    score INT NOT NULL,
    percentage INT NOT NULL,
    time_taken_seconds INT NOT NULL,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attempt_player FOREIGN KEY (player_id)
        REFERENCES players(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_attempts_player ON quiz_attempts(player_id);
CREATE INDEX IF NOT EXISTS idx_attempts_category ON quiz_attempts(category);
CREATE INDEX IF NOT EXISTS idx_attempts_completed_at ON quiz_attempts(completed_at);

-- Table 3: Quiz Answer Details
CREATE TABLE IF NOT EXISTS quiz_answer_details (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id INT NOT NULL,
    question_id INT NOT NULL,
    question_text VARCHAR(1000) NOT NULL,
    selected_answer VARCHAR(255) NULL,
    correct_answer VARCHAR(255) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    explanation TEXT,
    CONSTRAINT fk_detail_attempt FOREIGN KEY (attempt_id)
        REFERENCES quiz_attempts(attempt_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_details_attempt ON quiz_answer_details(attempt_id);

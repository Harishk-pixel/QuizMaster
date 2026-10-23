# QuizMaster — Quiz Application with Score Tracking

**QuizMaster** is a commercial-grade, modern desktop application built using Java 17+ and Java Swing. Designed with a sleek SaaS dashboard aesthetic, it replaces default Swing styling with an antialiased dark design system, responsive card layouts, and a centralized `CardLayout` navigation controller.

---

## 🚀 Phase 3 Implementation Highlights (MySQL Database Integration via JDBC)

Phase 3 permanently integrates MySQL relational persistence using JDBC, enabling persistent score history, historical attempt inspection, transactional atomic saves, and resilient offline graceful degradation:

1. **Relational Database Design (`quizmaster_db`)**:
   - **`players` Table**: Stores unique player records with auto-increment `player_id`, unique `player_name`, and registration timestamp. Automatically reuses existing player records across sessions.
   - **`quiz_attempts` Table**: Stores full attempt summaries (category, difficulty, total questions, correct/incorrect/unanswered counts, score, accuracy percentage, elapsed duration, completion timestamp) with foreign key linking to `players(player_id)` and cascading deletion.
   - **`quiz_answer_details` Table**: Persists per-question snapshots for completed quizzes (question ID, snapshot text, selected answer, correct answer, correctness boolean, and explanation) with foreign key linking to `quiz_attempts(attempt_id)` with `ON DELETE CASCADE`. Supports both answered and unanswered questions (`selected_answer = NULL`).

2. **Transactional All-or-Nothing Persistence**:
   - Managed by `QuizPersistenceService`: disables auto-commit, coordinates player reuse/insert, attempt insert with generated key retrieval, and batch answer details insertion.
   - Automatically executes `conn.rollback()` on any failure, ensuring zero orphaned records or incomplete attempts.

3. **Resilient Offline Mode & Graceful Degradation**:
   - If MySQL is stopped or unreachable, the application continues to run without crashing or throwing unhandled exceptions.
   - Preserves completed quiz results in memory so players never lose their scores.
   - Displays real-time database connection badges:
     - `● MySQL: Connected (XXms)` in vibrant green when database is active.
     - `○ MySQL: Offline (Local Mode)` in cautionary gold when database is unavailable.
   - Results screen displays an explicit status card (`💾 Saved to Database (Attempt #X)` or `⚠ Offline Mode (Saved locally in memory)`) with an interactive **"Retry Database Save"** button.

4. **Persistent Score History & Historical Answer Review**:
   - `ScoreHistoryPanel` queries live records from MySQL ordered newest first (`ORDER BY completed_at DESC`), with category filtering, real-time player search, and a manual **Refresh** action.
   - **Review Attempt Feature**: Selecting any attempt row in `ScoreHistoryPanel` and clicking **"Review Attempt 🔍"** loads the full question-by-question breakdown from `quiz_answer_details` into `AnswerReviewPanel`, dynamically configuring the back navigation to return seamlessly to Score History.

5. **Security & Configuration Management**:
   - Database connection settings configurable via environment variables:
     - `QUIZ_DB_URL` (defaults to `jdbc:mysql://localhost:3306/quizmaster_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`)
     - `QUIZ_DB_USER` (defaults to `root`)
     - `QUIZ_DB_PASSWORD` (defaults to empty `""`)
   - **Protected Credentials**: Passwords are never logged or exposed in UI error dialogs (masked as `password=****` in `DatabaseConfig.getMaskedSummary()`).
   - **Non-Destructive Initialization**: `DatabaseInitializer` executes `schema.sql` with `IF NOT EXISTS` guards; it never drops or truncates existing player tables.

## 🚀 Phase 4 — Final Polish, Testing & Project Completion

Phase 4 delivers final polish, reliability enhancements, shaded standalone packaging, comprehensive documentation, and complete workflow verification:

1. **Self-Contained Standalone Executable (Uber JAR)**:
   - Configured `maven-shade-plugin` 3.5.2 to package `mysql-connector-j` 8.3.0 and all dependencies directly into `target/quizmaster-1.0.0-SNAPSHOT.jar`.
   - The application can now be launched directly with `java -jar quizmaster-1.0.0-SNAPSHOT.jar` on any machine with Java 17+ without setting external classpaths.

2. **Null Safety & Edge Case Resilience**:
   - Hardened `ScoreHistoryPanel` against potential null categories or difficulties.
   - Guaranteed full multi-attempt session resetting so that starting a new quiz starts with 0 recorded answers, fresh question sequence, and a dedicated timer instance.

3. **Complete Workflow Verification**:
   - Verified 15-step user journey: Launch -> Setup -> Validation -> Answering -> Back/Forward Preservation -> Timer Expiration/Submission -> Results -> Persistence Retry -> Review Attempt -> Score History -> Starting New Fresh Session.

4. **Comprehensive Documentation Suite**:
   - [`docs/PROJECT_ARCHITECTURE.md`](file:///d:/anti%20gravity/Quiz%20Application%20with%20Score%20Tracking/docs/PROJECT_ARCHITECTURE.md): Layered & MVC architecture diagrams, sequence diagrams, schema ER diagram.
   - [`docs/USER_GUIDE.md`](file:///d:/anti%20gravity/Quiz%20Application%20with%20Score%20Tracking/docs/USER_GUIDE.md): Complete end-user instructions, scoring metrics, and troubleshooting steps.
   - [`docs/TEST_REPORT.md`](file:///d:/anti%20gravity/Quiz%20Application%20with%20Score%20Tracking/docs/TEST_REPORT.md): Audit results for all 25 automated tests, build verification, and live MySQL environment reporting.
   - [`docs/VIVA_QUESTIONS.md`](file:///d:/anti%20gravity/Quiz%20Application%20with%20Score%20Tracking/docs/VIVA_QUESTIONS.md): 25 project-specific viva questions and model answers covering Swing, OOP, JDBC, Transactions, and Testing.

---

## 🎨 Visual Design Tokens

| Element | Hex Code | Purpose |
|---|---|---|
| **Background Dark** | `#0B1020` | Primary application canvas |
| **Secondary Surface** | `#121A2B` | Navigation bars, input fields, and pill backgrounds |
| **Card Surface** | `#172238` | Metric tiles, question cards, and feature containers |
| **Primary Accent** | `#7C5CFC` | Primary call-to-actions, active indicators, and focus outlines |
| **Secondary Accent**| `#35B6FF` | Brand tags, category badges, and secondary buttons |
| **Main Text** | `#F4F7FC` | High-contrast headings and prominent body copy |
| **Muted Text** | `#9AA8BD` | Supporting labels, descriptions, and metadata |
| **Success** | `#35C98A` | Correct answers, passing scores, connected database badge |
| **Error** | `#FF647C` | Incorrect answers, timer warnings, validation alerts |
| **Warning** | `#FFB039` | Medium difficulty badges, offline database badge |

---

## 📁 Project Architecture

```
Quiz Application with Score Tracking/
├── pom.xml                               # Maven build configuration (MySQL Connector 8.3.0, H2 test, JUnit 5)
├── mvnw.cmd                              # Maven wrapper batch script
├── run.bat                               # One-click desktop launcher
├── README.md                             # Comprehensive documentation & verification report
└── src/
    ├── main/
    │   ├── resources/
    │   │   └── db/
    │   │       └── schema.sql            # DDL script creating players, quiz_attempts & quiz_answer_details
    │   └── java/
    │       └── com/quizmaster/
    │           ├── Main.java             # Application entry point (EDT, L&F initialization)
    │           ├── database/
    │           │   ├── DatabaseConfig.java      # Environment variable loader & masked credential summaries
    │           │   ├── DatabaseConnection.java  # JDBC connection manager & non-destructive health checks
    │           │   └── DatabaseInitializer.java # Non-destructive schema executor from schema.sql
    │           ├── repository/
    │           │   ├── PlayerRepository.java     # Player creation and deduplication queries
    │           │   ├── QuizAttemptRepository.java# Attempt insertions, latest-first queries, and filters
    │           │   └── QuizAnswerRepository.java # Batch answer detail inserts & review item reconstruction
    │           ├── service/
    │           │   ├── QuizPersistenceService.java # Transactional all-or-nothing save & query routing
    │           │   ├── QuestionBankService.java    # 75+ multiple-choice question bank across 5 categories
    │           │   ├── QuizTimerService.java       # Swing countdown timer & warning thresholds
    │           │   ├── ScoreCalculator.java        # Scoring engine & accuracy formulas
    │           │   └── QuizService.java            # Quiz lifecycle, duplicate submission guard & retry hooks
    │           ├── model/
    │           │   ├── AppState.java               # Central state, DB synchronization & attempt selection
    │           │   ├── Category.java               # Quiz categories with symbols & metadata
    │           │   ├── Difficulty.java             # Difficulty levels with badge color tokens
    │           │   ├── Question.java               # Immutable question model (4 options validation)
    │           │   ├── QuizAttempt.java            # Historical attempt record with DB ID & player metadata
    │           │   └── QuizResult.java             # Session result, AnswerStatus, persistence status tracking
    │           ├── controller/
    │           │   └── NavigationController.java   # CardLayout & navigation history stack
    │           ├── util/
    │           │   ├── UIConstants.java            # Typography, color tokens, and layout measurements
    │           │   ├── Theme.java                  # Look and feel overrides & component factories
    │           │   ├── RoundedPanel.java           # Antialiased custom JPanel with rounded corners & borders
    │           │   ├── RoundedButton.java          # Custom JButton supporting Primary, Secondary, Outline, Danger
    │           │   ├── CircularProgressIndicator.java # Custom-painted score accuracy progress ring
    │           │   └── CustomScrollBarUI.java      # Modern dark slim scrollbars
    │           └── view/
    │               ├── MainFrame.java              # Top-level window (1280x800, min 1000x650)
    │               ├── HomePanel.java              # Landing hero screen with feature highlights
    │               ├── PlayerSetupPanel.java       # Category, difficulty & question count selector
    │               ├── QuizPanel.java              # Interactive assessment screen & timer badge
    │               ├── ResultPanel.java            # Score card, circular gauge, and database status card
    │               ├── ScoreHistoryPanel.java      # Attempt table, MySQL badge, refresh & review attempt button
    │               └── AnswerReviewPanel.java      # Detailed review supporting both current & historical attempts
    └── test/
        └── java/
            └── com/quizmaster/
                ├── QuizDatabaseTest.java           # Automated tests for Config, Initializer, Repos, Rollback & Offline
                ├── QuizMasterTest.java             # Model, QuestionBank, Scoring & Timer tests
                └── QuizMasterUISimulationTest.java # Full EDT UI lifecycle & simulation test
```

---

## 🗄️ Database Setup Instructions

### 1. Prerequisites
- **MySQL Server**: MySQL 8.0 or higher (or MariaDB 10.5+).
- Ensure the MySQL service is started on port `3306`.

### 2. Create Database and Tables
You can initialize the database using the MySQL Command Line Client or MySQL Workbench:

```sql
-- 1. Create the database
CREATE DATABASE IF NOT EXISTS quizmaster_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. Select the database
USE quizmaster_db;

-- 3. Execute the schema script
SOURCE src/main/resources/db/schema.sql;
```

Alternatively, when QuizMaster starts with valid MySQL credentials, `DatabaseInitializer` will automatically detect the database and execute `schema.sql` idempotently.

### 3. Configure Connection Environment Variables (Optional)
If your MySQL configuration differs from the local default (`root` with blank password), configure the following environment variables:

#### In Windows PowerShell:
```powershell
$env:QUIZ_DB_URL = "jdbc:mysql://localhost:3306/quizmaster_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:QUIZ_DB_USER = "root"
$env:QUIZ_DB_PASSWORD = "your_actual_password"
```

#### In Windows Command Prompt:
```cmd
set QUIZ_DB_URL=jdbc:mysql://localhost:3306/quizmaster_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
set QUIZ_DB_USER=root
set QUIZ_DB_PASSWORD=your_actual_password
```

### 4. Offline / No Database Mode
If MySQL is not installed or not currently running, **QuizMaster operates seamlessly in Offline Mode**:
- Scores and attempts are cached in memory.
- The UI displays `○ MySQL: Offline (Local Mode)`.
- Completed quizzes show an offline notice on the results screen.
- When MySQL is started later, the player can click **"Retry Database Save"** to persist the result without re-taking the quiz.

---

## 🚀 How to Build and Run

### Prerequisites
- **Java Development Kit (JDK)**: JDK 17 or higher (tested with JDK 26).
- **Maven**: Version 3.8+ (included via `mvnw.cmd`).

### 1. Build and Run via Maven
```powershell
# Compile all source files
.\mvnw.cmd clean compile

# Run the automated test suite (all 25 tests)
.\mvnw.cmd test

# Package the application into a self-contained executable JAR
.\mvnw.cmd package

# Run the desktop application
java -jar target\quizmaster-1.0.0-SNAPSHOT.jar
```

### 2. One-Click Execution (Windows)
Double-click [`run.bat`](file:///d:/anti%20gravity/Quiz%20Application%20with%20Score%20Tracking/run.bat) in the project root folder.

---

## 🧪 Verification & Test Results

The automated test suite contains **25 comprehensive unit and integration tests**:

| Test Class | Test Method | Target Area | Result |
|---|---|---|---|
| **QuizDatabaseTest** | `testDatabaseConfigSecurityAndDefaults` | Safe development defaults, custom credential loading, password masking (`****`) | **PASSED** |
| **QuizDatabaseTest** | `testDatabaseConnectionStatus` | Active connection health checks, unreachable server error sanitization | **PASSED** |
| **QuizDatabaseTest** | `testDatabaseInitializerIdempotence` | Schema execution from `schema.sql`, table creation, idempotent re-runs | **PASSED** |
| **QuizDatabaseTest** | `testPlayerRepositoryOperations` | Unique player creation, player record reuse, whitespace trimming, anonymous fallback | **PASSED** |
| **QuizDatabaseTest** | `testQuizAttemptRepositoryQueries` | Attempt insertion, newest-first ordering (`findAll`), category and player filters | **PASSED** |
| **QuizDatabaseTest** | `testQuizAnswerRepositoryAnswerDetails` | Batch details insert, `NULL` handling for unanswered questions, review reconstruction | **PASSED** |
| **QuizDatabaseTest** | `testFullQuizResultPersistenceSuccess` | All-or-nothing save across `players`, `quiz_attempts`, `quiz_answer_details`, duplicate save guard | **PASSED** |
| **QuizDatabaseTest** | `testTransactionRollbackOnFailure` | Verifies full transactional rollback on constraint violation with zero orphaned records | **PASSED** |
| **QuizDatabaseTest** | `testOfflineDatabaseHandling` | Graceful degradation on unreachable server, offline flags, zero uncaught exceptions | **PASSED** |
| **QuizDatabaseTest** | `testPersistenceRetryWorkflow` | Validates retry persistence hook when an unpersisted result is subsequently saved | **PASSED** |
| **QuizDatabaseTest** | `testLiveMySQLStatusHonestReporting` | Honestly checks the local workstation environment for live MySQL and reports status | **PASSED** |
| **QuizMasterTest** | `testQuestionBankIntegrity` | Validates 75 questions, unique IDs, exactly 4 options, valid answers, explanations | **PASSED** |
| **QuizMasterTest** | `testScoreCalculationAllCorrect` | Evaluates 5/5 correct answers -> Score = 5, Percentage = 100%, 0 incorrect, 0 unanswered | **PASSED** |
| **QuizMasterTest** | `testScoreCalculationAllIncorrect` | Evaluates 0/5 answers -> Score = 0, Percentage = 0%, 5 incorrect, 0 unanswered | **PASSED** |
| **QuizMasterTest** | `testScoreCalculationWithUnanswered` | Evaluates partial answers with unanswered questions -> 2 correct, 1 incorrect, 2 unanswered (40%) | **PASSED** |
| **QuizMasterTest** | `testEmptySessionZeroQuestions` | Edge case validation for empty/zero question session (no division-by-zero) | **PASSED** |
| **QuizMasterTest** | `testQuestionNavigationAndAnswerPreservation` | Verifies forward/backward navigation and answer preservation across steps | **PASSED** |
| **QuizMasterTest** | `testTimerExpirationAndCleanup` | Verifies total duration (150s), tick events, warning threshold at 60s, expiration callback | **PASSED** |
| **QuizMasterTest** | `testQuizSubmissionAndDuplicateProtection` | Verifies atomic submission, timer halt, history recording, duplicate submission prevention | **PASSED** |
| **QuizMasterTest** | `testStartingNewQuizResetsSession` | Verifies starting new quiz clears previous answers, resets index, starts fresh timer | **PASSED** |
| **QuizMasterTest** | `testInsufficientQuestionsValidation` | Verifies requesting more questions than available throws clean `IllegalStateException` | **PASSED** |
| **QuizMasterTest** | `testNavigationStack` | Controller navigation stack, screen transitions, and `navigateBack()` hierarchy | **PASSED** |
| **QuizMasterTest** | `testCustomUIComponents` | Instantiation of `RoundedPanel`, `RoundedButton`, `CircularProgressIndicator`, and `Theme` | **PASSED** |
| **QuizMasterUISimulationTest** | `testFullUINavigationAndLifecycle` | Full Swing EDT UI simulation across all 6 screens, session submission, responsive window resizing | **PASSED** |
| **QuizMasterUISimulationTest** | `testSequentialSessionsAndHistoricalReviewWorkflow` | Session 1 submission, navigation to Score History, historical attempt review, clean Session 2 | **PASSED** |

> **Testing Environment Note**: Automated database tests run against an isolated in-memory H2 database configured in MySQL-compatibility mode (`MODE=MySQL;DATABASE_TO_LOWER=TRUE`). This guarantees thorough integration testing of SQL queries, transactions, rollbacks, and repositories without altering or deleting any live user databases.

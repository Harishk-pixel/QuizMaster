# QuizMaster — College Project Viva Questions & Answers

This document provides 25 project-specific technical viva questions and concise, accurate answers for the **QuizMaster** desktop application.

---

### Category 1: Java Swing & Desktop GUI

#### Q1: Why did you use `CardLayout` for screen navigation instead of opening multiple `JFrame` windows?
**Answer:** `CardLayout` allows swapping multiple panels (`JPanel`) inside a single top-level `JFrame`. This approach avoids flickering, prevents multiple taskbar icons, preserves application state across screens, and provides a smoother, modern desktop experience similar to modern web and SaaS applications.

#### Q2: What is the Event Dispatch Thread (EDT) and why is it important in Swing?
**Answer:** The Event Dispatch Thread (EDT) is a single dedicated thread in Java Swing responsible for processing GUI events (such as button clicks and mouse movements) and painting components. All UI creation and updates must run on the EDT (via `SwingUtilities.invokeLater`) to prevent race conditions and thread-safety violations.

#### Q3: How did you implement custom UI components like rounded buttons and custom progress rings?
**Answer:** By subclassing standard Swing components (like `JButton` and `JPanel`) and overriding the `paintComponent(Graphics g)` method. We cast `Graphics` to `Graphics2D`, enable antialiasing with `RenderingHints.KEY_ANTIALIASING`, and use methods like `fillRoundRect()` and `drawArc()` to draw smooth, modern vector shapes.

#### Q4: How is responsive layout handled when the window is resized from 1000×650 to 1440×900?
**Answer:** We used dynamic layout managers such as `BorderLayout`, `BoxLayout`, `GridLayout`, and `GridBagLayout` combined with `JScrollPane` instead of absolute positioning (`null` layout). Panels resize proportionally and scrollbars appear automatically if the window becomes smaller than the content.

---

### Category 2: Object-Oriented Programming (OOP) & Architecture

#### Q5: How is the MVC (Model-View-Controller) design pattern applied in QuizMaster?
**Answer:** 
- **Model** (`com.quizmaster.model`): Holds domain data such as `Question`, `QuizResult`, and `QuizAttempt`.
- **View** (`com.quizmaster.view`): Responsible purely for rendering Swing UI components and displaying information to the user.
- **Controller & Service** (`com.quizmaster.controller` and `com.quizmaster.service`): Coordinates user input, routes navigation via `NavigationController`, and executes business rules in `QuizService` and `ScoreCalculator`.

#### Q6: How does QuizMaster demonstrate the concept of Encapsulation?
**Answer:** All fields in model and service classes (like `Question`, `QuizResult`, and `QuizService`) are marked `private` or `final`. State access and mutation occur through controlled getters and public business methods. Lists returned from getters (such as question options) are wrapped in `Collections.unmodifiableList` to prevent external modification.

#### Q7: What is the benefit of separating `QuestionBankService` from the UI code?
**Answer:** Separation of concerns. The UI only asks for questions by category, difficulty, and count without needing to know how questions are stored, formatted, or validated. This allows updating or replacing the question bank (e.g., loading from JSON or an external API) without modifying a single line of UI code.

#### Q8: How does the Observer / Listener pattern work in the countdown timer?
**Answer:** `QuizTimerService` defines listener interfaces: `TimerTickListener` and `TimerTimeoutListener`. The `QuizPanel` registers callbacks to these interfaces. Every second, the timer fires tick notifications, and when time expires, it invokes `onTimeout()`. This decouples the timer mechanism from the UI view.

---

### Category 3: JDBC, MySQL & Database Integration

#### Q9: What is JDBC and what role does `mysql-connector-j` play?
**Answer:** Java Database Connectivity (JDBC) is a Java standard API that defines how Java applications interact with relational databases. `mysql-connector-j` is the official MySQL JDBC driver; it translates standard JDBC interface calls into MySQL's network protocol over TCP/IP (port 3306).

#### Q10: Why did you use `PreparedStatement` instead of `Statement`?
**Answer:** 
1. **Security**: `PreparedStatement` pre-compiles SQL queries and treats input parameters strictly as data values, completely preventing SQL injection vulnerabilities.
2. **Performance**: Database engines compile and cache the query execution plan once, making repeated queries faster.
3. **Data Type Handling**: It handles NULLs, dates, timestamps, and escaping special characters automatically.

#### Q11: Explain the relational database schema of `quizmaster_db`.
**Answer:** The database contains three related tables:
1. `players`: Stores unique player handles (`player_id`, `player_name`).
2. `quiz_attempts`: Stores completed quiz headers (`attempt_id`, foreign key `player_id`, score, percentage, duration, timestamp).
3. `quiz_answer_details`: Stores per-question answer snapshots (`detail_id`, foreign key `attempt_id`, selected answer, correct answer, correctness boolean).

#### Q12: What is the purpose of `ON DELETE CASCADE` in your foreign keys?
**Answer:** `ON DELETE CASCADE` ensures referential integrity. If a record in `players` or `quiz_attempts` is deleted, MySQL automatically deletes all related records in `quiz_attempts` and `quiz_answer_details`, preventing orphaned child records.

#### Q13: How does player deduplication work in `PlayerRepository`?
**Answer:** The `findOrCreate` method queries `players` by `player_name`. If a row exists, it retrieves the existing `player_id`. If not found, it inserts a new player record and retrieves the generated key. This prevents duplicate player records across multiple quiz attempts.

---

### Category 4: Transactions, Rollback & Reliability

#### Q14: What is a database transaction, and how did you implement it in QuizMaster?
**Answer:** A transaction is a sequence of database operations treated as a single atomic unit (all-or-nothing). In `QuizPersistenceService`, we call `conn.setAutoCommit(false)`. We then execute inserts for the player, attempt header, and answer details. If all succeed, we call `conn.commit()`. If any operation fails, we execute `conn.rollback()` in the catch block.

#### Q15: Why is transaction rollback essential when saving a quiz result?
**Answer:** A quiz submission writes to two or three tables (`players`, `quiz_attempts`, and `quiz_answer_details`). If an error occurs while writing the answer details after inserting the attempt header, a non-transactional database would be left with an incomplete attempt having zero answer details. Rollback ensures that either all data is saved or none of it is saved.

#### Q16: How does QuizMaster handle an offline or unreachable MySQL server?
**Answer:** The application uses **Graceful Degradation**. `DatabaseConnection.testConnection()` checks server connectivity. If MySQL is unavailable, `QuizPersistenceService` catches the exception, marks the in-memory result as `persisted = false`, and keeps the quiz results in memory. The UI displays an offline badge (`○ MySQL: Offline`) and a **"Retry Save"** button so the user can save once the database is back online.

#### Q17: How did you ensure database credentials (passwords) are protected?
**Answer:** Passwords can be loaded from environment variables (`QUIZ_DB_PASSWORD`) rather than hardcoded in the codebase. In error dialogs and `DatabaseConfig.toString()`, credentials are sanitized and masked as `password=****`.

---

### Category 5: Quiz Logic, Timer & Scoring

#### Q18: How does QuizMaster prevent accidental duplicate quiz submissions?
**Answer:** `QuizService` maintains a synchronized boolean flag `submitted`. Once `submitQuiz()` is called, subsequent calls immediately return the cached `lastResult` without re-running calculations, re-inserting attempts into the history list, or creating duplicate rows in MySQL.

#### Q19: How are unanswered questions treated in scoring and in the database?
**Answer:** Unanswered questions receive 0 points (no negative marking). In the database, they are saved in `quiz_answer_details` with `selected_answer = NULL` and `is_correct = false`. This allows the Answer Review screen to clearly label them as `⊘ UNANSWERED`.

#### Q20: What happens to the countdown timer when the player leaves early or submits before time expires?
**Answer:** In `QuizService.submitQuiz()` and `cancelQuiz()`, the `timerService.stop()` method is called immediately. This stops the underlying `javax.swing.Timer` and cleans up listeners, preventing background timer threads from firing after the session ends.

---

### Category 6: Exception Handling, Testing & Deployment

#### Q21: What is the difference between Checked and Unchecked Exceptions in Java? Give an example from your project.
**Answer:**
- **Checked Exceptions**: Exceptions checked at compile time; must be caught or declared in a `throws` clause. Example: `SQLException` in JDBC operations.
- **Unchecked Exceptions** (`RuntimeException`): Exceptions occurring at runtime due to programming bugs or invalid arguments. Example: `IllegalArgumentException` thrown when a player name is empty or question count is invalid.

#### Q22: Why did you use try-with-resources in your repositories?
**Answer:** The try-with-resources statement automatically closes resources that implement `AutoCloseable` (such as `Connection`, `PreparedStatement`, and `ResultSet`) at the end of the block, even if an exception occurs. This guarantees that database cursors and socket connections are not leaked.

#### Q23: How did you test database code without risking existing user databases?
**Answer:** We used an isolated in-memory **H2 database** configured in MySQL compatibility mode (`MODE=MySQL`) in the test scope. H2 creates tables in RAM for the duration of the test suite and destroys them upon completion, ensuring unit tests run fast, deterministically, and never touch live production databases.

#### Q24: What is the purpose of the `maven-shade-plugin` in `pom.xml`?
**Answer:** The standard `maven-jar-plugin` only packages your compiled classes into a JAR; it does not include external dependencies like `mysql-connector-j`. The `maven-shade-plugin` bundles all dependency classes into a single **uber/fat executable JAR**, allowing users to launch the application with a simple `java -jar` command without configuring a classpath.

#### Q25: How do you verify that starting a second quiz session does not reuse answers from the previous session?
**Answer:** In `QuizService.startQuiz()`, the session state is explicitly reset: `userAnswers.clear()`, `currentQuestionIndex = 0`, `submitted = false`, and `lastResult = null`. We verified this with an automated test in `QuizMasterUISimulationTest.testSequentialSessionsAndHistoricalReviewWorkflow()`.

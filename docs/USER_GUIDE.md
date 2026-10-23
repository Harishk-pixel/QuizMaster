# QuizMaster — Desktop Application User Guide

Welcome to **QuizMaster**, a professional Java desktop quiz and performance tracking platform. This guide explains how to operate the application, configure your quiz sessions, interpret scores, and troubleshoot database connection settings.

---

## 1. Getting Started

### Launching the Application
- **Option A (One-Click Windows)**: Double-click [`run.bat`](file:///d:/anti%20gravity/Quiz%20Application%20with%20Score%20Tracking/run.bat) in the project folder.
- **Option B (Command Line)**:
  ```powershell
  java -jar target\quizmaster-1.0.0-SNAPSHOT.jar
  ```
- The application window opens centered at **1280 × 800** pixels. It supports resizing down to **1000 × 650** and up to high-resolution displays (e.g. **1440 × 900** and above).

---

## 2. Setting Up a Quiz Session

1. From the **Welcome / Home Screen**, click **"Get Started"** or **"Start Quiz"**.
2. **Player Name**:
   - Enter your name or player handle (minimum 2 characters).
   - QuizMaster uses this name to link your scores in the persistent database.
3. **Select Quiz Category**:
   - **General Knowledge**: History, Geography, Inventions & World Trivia.
   - **Science**: Physics, Chemistry, Biology & Astronomy.
   - **Computer Science**: Algorithms, Data Structures, Operating Systems & Networks.
   - **Mathematics**: Algebra, Arithmetic, Geometry, Probability & Logic.
   - **Programming**: Java, OOP, Syntax, JVM & Software Design.
4. **Choose Difficulty Level**:
   - **Easy**: Core definitions and fundamental facts.
   - **Medium**: Applied concepts and standard problem solving.
   - **Hard**: Advanced theory, architecture, and edge cases.
5. **Question Count**:
   - Choose **5**, **10**, or **15** questions.
6. Click **"Start Quiz Session →"** to begin.

---

## 3. Taking the Quiz

### Question Card Navigation
- **Selecting an Answer**: Click anywhere on one of the four answer option cards (A, B, C, or D). The chosen option is highlighted in purple with a radio dot.
- **Moving Forward/Backward**: Click **"Next Question →"** or **"← Previous"** at the bottom of the screen.
- **Answer Preservation**: Your selections are securely saved as you navigate between questions. You can go back and change any answer before submission.
- **Progress Tracking**: The top bar displays your current question (e.g., *"Question 3 of 10"*) and a visual progress indicator.

### Using the Countdown Timer
- The quiz allocates **30 seconds per question** (e.g., 5 questions = 2 minutes 30 seconds).
- The countdown appears at the top right of the screen in `MM:SS` format.
- **Warning Threshold**: When **60 seconds** remain, the timer changes to cautionary red with a `⚠ (Hurry!)` indicator.
- **Auto-Submission**: If the timer reaches `00:00`, a dialog notifies you that time has expired, and your answers are automatically submitted.

### Submitting the Quiz
- On the final question, click **"Submit Quiz"**.
- A confirmation dialog appears showing:
  - Total questions in the quiz.
  - Number of answered questions.
  - Number of unanswered questions.
- If you have left questions unanswered, you can click **"No"** to return and complete them, or **"Yes"** to finalize your attempt.

---

## 4. Reviewing Your Results

### Score & Performance Breakdown
Upon submission, the **Result Screen** showcases:
- **Accuracy Circular Ring**: Visual representation of your accuracy percentage.
- **Performance Evaluation**: Custom feedback message based on your achievement:
  - *100%*: 🏆 Outstanding Mastery!
  - *80–99%*: 🌟 Excellent Job!
  - *60–79%*: 👍 Good Effort!
  - *<60%*: 📚 Keep Practicing!
- **Metric Tiles**: Total Score, Correct count, Incorrect count, and Unanswered count.
- **Time Elapsed**: Total duration taken in minutes and seconds.

### Persistence Status Badge
- **Saved to MySQL**: A green badge `💾 Saved to MySQL (Attempt #QM-X)` confirms your attempt is permanently stored in the database.
- **Offline Mode**: If MySQL was offline during submission, a gold badge `⚠ Offline Mode` appears along with an interactive **"Retry Save"** button.

### Detailed Answer Review
Click **"Review Answers"** to inspect a question-by-question post-quiz audit:
- All 4 options are displayed for each question.
- Your selection is clearly marked (`(Your Pick ✓)` in green or `(Your Pick ✕)` in red).
- The correct option is highlighted in green.
- Unanswered questions are tagged with `⊘ UNANSWERED`.
- An **Explanation Box** explains why the correct answer is right.

---

## 5. Performance & Score History

Access your full attempt history by clicking **"Score History"** in the top navigation or on the Results screen.

- **Metric Tiles**: Highlights your **Highest Score**, **Average Score percentage**, and **Total Completed Attempts**.
- **Historical Table**: Displays Attempt ID, Player Name, Date & Time, Category, Difficulty, Score, Percentage, and Time Taken.
- **Filter by Category**: Use the dropdown filter to view attempts from a specific subject.
- **Search Bar**: Type any player name or attempt ID to filter rows instantly.
- **Review Historical Attempt**:
  1. Click any row in the score history table.
  2. Click **"Review Attempt 🔍"**.
  3. The full question breakdown and historical answer snapshot are loaded from the MySQL database into the review screen.
  4. Click **"← Back to History"** to return to the score table.

---

## 6. Resolving Common Errors & Troubleshooting

### 1. Database Connection Offline (`○ MySQL: Offline`)
- **Cause**: MySQL server is not running on your machine or is running on a port other than `3306`.
- **Solution**:
  1. Open Windows Services (`services.msc`) and ensure the **MySQL80** or **MySQL** service is **Running**.
  2. Or start it via administrative command prompt:
     ```cmd
     net start mysql
     ```
  3. In QuizMaster, navigate to **Score History** and click **"🔄 Refresh"**. The badge will turn green (`● MySQL: Connected`).

### 2. Authentication Failed (`Access denied for user`)
- **Cause**: MySQL root password differs from the blank default.
- **Solution**: Configure the `QUIZ_DB_PASSWORD` environment variable before launching:
  ```powershell
  $env:QUIZ_DB_USER = "root"
  $env:QUIZ_DB_PASSWORD = "your_actual_password"
  java -jar target\quizmaster-1.0.0-SNAPSHOT.jar
  ```

### 3. Database Does Not Exist (`Unknown database 'quizmaster_db'`)
- **Cause**: Database has not yet been created.
- **Solution**: Run MySQL Command Line Client:
  ```sql
  CREATE DATABASE quizmaster_db;
  ```
  Then restart QuizMaster; `DatabaseInitializer` will automatically construct all necessary tables.

### 4. Player Name Validation Error
- **Cause**: Player name is left blank or is shorter than 2 characters.
- **Solution**: Enter a valid name containing at least 2 characters.

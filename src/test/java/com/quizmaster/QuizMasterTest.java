package com.quizmaster;

import com.quizmaster.controller.NavigationController;
import com.quizmaster.model.AppState;
import com.quizmaster.model.Category;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.model.QuizAttempt;
import com.quizmaster.model.QuizResult;
import com.quizmaster.service.QuestionBankService;
import com.quizmaster.service.QuizService;
import com.quizmaster.service.QuizTimerService;
import com.quizmaster.service.ScoreCalculator;
import com.quizmaster.util.CircularProgressIndicator;
import com.quizmaster.util.RoundedButton;
import com.quizmaster.util.RoundedPanel;
import com.quizmaster.util.Theme;
import com.quizmaster.util.UIConstants;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuizMasterTest {

    private AppState appState;
    private QuestionBankService questionBankService;
    private ScoreCalculator scoreCalculator;
    private QuizService quizService;

    @BeforeEach
    public void setUp() {
        appState = new AppState();
        questionBankService = appState.getQuestionBankService();
        quizService = appState.getQuizService();
        scoreCalculator = new ScoreCalculator();
    }

    // ==========================================
    // 1. Question Bank Validation Tests
    // ==========================================
    @Test
    @DisplayName("Question Bank contains at least 50 questions, exactly 4 options, valid answers, and no duplicates")
    public void testQuestionBankIntegrity() {
        List<Question> allQuestions = questionBankService.getAllQuestions();
        assertNotNull(allQuestions);
        assertTrue(allQuestions.size() >= 50, "Question bank must contain at least 50 questions (found " + allQuestions.size() + ")");

        Set<Integer> seenIds = new HashSet<>();
        Set<String> seenTexts = new HashSet<>();

        for (Question q : allQuestions) {
            // Unique ID
            assertTrue(q.getId() > 0, "ID must be positive");
            assertTrue(seenIds.add(q.getId()), "Duplicate question ID found: " + q.getId());

            // Unique question text
            assertNotNull(q.getQuestionText());
            assertFalse(q.getQuestionText().trim().isEmpty());
            assertTrue(seenTexts.add(q.getQuestionText().trim().toLowerCase()),
                    "Duplicate question text found: " + q.getQuestionText());

            // Category & Difficulty
            assertNotNull(q.getCategory(), "Question category cannot be null");
            assertNotNull(q.getDifficulty(), "Question difficulty cannot be null");

            // Exactly 4 options
            List<String> options = q.getOptions();
            assertNotNull(options);
            assertEquals(4, options.size(), "Question " + q.getId() + " must have exactly 4 options");
            for (String opt : options) {
                assertNotNull(opt);
                assertFalse(opt.trim().isEmpty(), "Option in question " + q.getId() + " cannot be blank");
            }

            // Exactly one valid correct answer
            int correctIdx = q.getCorrectOptionIndex();
            assertTrue(correctIdx >= 0 && correctIdx < 4, "Correct index must be 0, 1, 2, or 3");
            assertEquals(options.get(correctIdx), q.getCorrectAnswerText(), "Correct answer text must match option at index");

            // Explanation
            assertNotNull(q.getExplanation());
            assertFalse(q.getExplanation().trim().isEmpty(), "Question " + q.getId() + " must have an explanation");
        }

        // Verify representation across all 5 categories
        for (Category cat : Category.values()) {
            List<Question> catQuestions = questionBankService.getQuestionsByCategory(cat);
            assertFalse(catQuestions.isEmpty(), "Category " + cat.getDisplayName() + " must have questions");
            assertTrue(catQuestions.size() >= 10, "Category " + cat.getDisplayName() + " should have at least 10 questions");
        }

        // Verify representation across all 3 difficulties
        for (Difficulty diff : Difficulty.values()) {
            List<Question> diffQuestions = questionBankService.getQuestions(Category.PROGRAMMING, diff);
            assertFalse(diffQuestions.isEmpty(), "Difficulty " + diff.getDisplayName() + " must have questions");
        }
    }

    // ==========================================
    // 2. Score Calculation Tests
    // ==========================================
    @Test
    @DisplayName("Score calculation: all correct answers yields full score and 100%")
    public void testScoreCalculationAllCorrect() {
        List<Question> questions = questionBankService.getQuestions(Category.PROGRAMMING, Difficulty.EASY, 5);
        assertEquals(5, questions.size());

        Map<Integer, Integer> answers = new HashMap<>();
        for (int i = 0; i < questions.size(); i++) {
            answers.put(i, questions.get(i).getCorrectOptionIndex());
        }

        QuizResult result = scoreCalculator.calculateResult("Champion", Category.PROGRAMMING, Difficulty.EASY, questions, answers, 45);

        assertEquals("Champion", result.getPlayerName());
        assertEquals(5, result.getTotalQuestions());
        assertEquals(5, result.getScore());
        assertEquals(5, result.getMaxScore());
        assertEquals(100, result.getPercentage());
        assertEquals(5, result.getCorrectCount());
        assertEquals(0, result.getIncorrectCount());
        assertEquals(0, result.getUnansweredCount());
        assertEquals(45, result.getTimeTakenSeconds());
        assertEquals("00:45", result.getTimeTakenFormatted());
        assertTrue(result.getPerformanceMessage().contains("100%"));
    }

    @Test
    @DisplayName("Score calculation: all incorrect answers yields 0 score and 0%")
    public void testScoreCalculationAllIncorrect() {
        List<Question> questions = questionBankService.getQuestions(Category.SCIENCE, Difficulty.MEDIUM, 5);
        Map<Integer, Integer> answers = new HashMap<>();

        for (int i = 0; i < questions.size(); i++) {
            int wrong = (questions.get(i).getCorrectOptionIndex() + 1) % 4;
            answers.put(i, wrong);
        }

        QuizResult result = scoreCalculator.calculateResult("Player", Category.SCIENCE, Difficulty.MEDIUM, questions, answers, 60);

        assertEquals(0, result.getScore());
        assertEquals(0, result.getPercentage());
        assertEquals(0, result.getCorrectCount());
        assertEquals(5, result.getIncorrectCount());
        assertEquals(0, result.getUnansweredCount());
    }

    @Test
    @DisplayName("Score calculation: handles unanswered questions and partial score accurately")
    public void testScoreCalculationWithUnanswered() {
        List<Question> questions = questionBankService.getQuestions(Category.MATHEMATICS, Difficulty.EASY, 5);
        Map<Integer, Integer> answers = new HashMap<>();

        // Q0: correct, Q1: correct, Q2: incorrect, Q3: unanswered, Q4: unanswered
        answers.put(0, questions.get(0).getCorrectOptionIndex());
        answers.put(1, questions.get(1).getCorrectOptionIndex());
        answers.put(2, (questions.get(2).getCorrectOptionIndex() + 1) % 4);
        // Q3 and Q4 left empty

        QuizResult result = scoreCalculator.calculateResult("Student", Category.MATHEMATICS, Difficulty.EASY, questions, answers, 80);

        assertEquals(2, result.getScore());
        assertEquals(5, result.getMaxScore());
        assertEquals(40, result.getPercentage()); // 2/5 = 40%
        assertEquals(2, result.getCorrectCount());
        assertEquals(1, result.getIncorrectCount());
        assertEquals(2, result.getUnansweredCount());

        List<QuizResult.ReviewItem> reviewItems = result.getReviewItems();
        assertEquals(QuizResult.AnswerStatus.CORRECT, reviewItems.get(0).getStatus());
        assertEquals(QuizResult.AnswerStatus.CORRECT, reviewItems.get(1).getStatus());
        assertEquals(QuizResult.AnswerStatus.INCORRECT, reviewItems.get(2).getStatus());
        assertEquals(QuizResult.AnswerStatus.UNANSWERED, reviewItems.get(3).getStatus());
        assertEquals(QuizResult.AnswerStatus.UNANSWERED, reviewItems.get(4).getStatus());
    }

    @Test
    @DisplayName("Percentage calculation edge cases: zero questions handled without division by zero")
    public void testEmptySessionZeroQuestions() {
        QuizResult result = scoreCalculator.calculateResult("EmptyPlayer", Category.GENERAL_KNOWLEDGE, Difficulty.EASY, null, null, 0);
        assertNotNull(result);
        assertEquals(0, result.getTotalQuestions());
        assertEquals(0, result.getScore());
        assertEquals(0, result.getPercentage());
    }

    // ==========================================
    // 3. Question Navigation and Answer Preservation
    // ==========================================
    @Test
    @DisplayName("Question navigation and answer preservation across back-and-forth steps")
    public void testQuestionNavigationAndAnswerPreservation() {
        quizService.startQuiz("NavTester", Category.COMPUTER_SCIENCE, Difficulty.EASY, 5);

        assertEquals(0, quizService.getCurrentQuestionIndex());
        assertFalse(quizService.hasPreviousQuestion());
        assertTrue(quizService.hasNextQuestion());

        // Answer Q0
        quizService.recordAnswer(0, 1);
        assertEquals(1, quizService.getUserAnswer(0));
        assertTrue(quizService.isCurrentQuestionAnswered());

        // Move to Q1
        assertTrue(quizService.nextQuestion());
        assertEquals(1, quizService.getCurrentQuestionIndex());
        assertTrue(quizService.hasPreviousQuestion());

        // Answer Q1
        quizService.recordAnswer(1, 3);
        assertEquals(3, quizService.getUserAnswer(1));

        // Move to Q2
        assertTrue(quizService.nextQuestion());
        assertEquals(2, quizService.getCurrentQuestionIndex());
        assertFalse(quizService.isCurrentQuestionAnswered()); // Unanswered yet

        // Navigate back to Q1 and Q0, verify answers are preserved
        assertTrue(quizService.previousQuestion());
        assertEquals(1, quizService.getCurrentQuestionIndex());
        assertEquals(3, quizService.getUserAnswer(1));

        assertTrue(quizService.previousQuestion());
        assertEquals(0, quizService.getCurrentQuestionIndex());
        assertEquals(1, quizService.getUserAnswer(0));

        // Verify answered and unanswered counts
        assertEquals(2, quizService.getAnsweredCount());
        assertEquals(3, quizService.getUnansweredCount());
    }

    // ==========================================
    // 4. Timer Service and Expiration
    // ==========================================
    @Test
    @DisplayName("Timer service calculates duration, warns at 60s, and handles expiration")
    public void testTimerExpirationAndCleanup() {
        QuizTimerService timer = new QuizTimerService();
        final boolean[] tickFired = {false};
        final boolean[] timeoutFired = {false};

        timer.addTickListener((remaining, formatted, isWarning) -> tickFired[0] = true);
        timer.addTimeoutListener(() -> timeoutFired[0] = true);

        // 5 questions * 30 seconds = 150 seconds
        timer.start(150);
        assertTrue(timer.isRunning());
        assertEquals(150, timer.getRemainingSeconds());
        assertEquals("02:30", timer.getFormattedRemainingTime());
        assertFalse(timer.isWarning());
        assertTrue(tickFired[0]);

        // Tick 90 seconds -> 60 seconds remaining -> should trigger warning
        for (int i = 0; i < 90; i++) {
            timer.tickOneSecond();
        }
        assertEquals(60, timer.getRemainingSeconds());
        assertEquals("01:00", timer.getFormattedRemainingTime());
        assertTrue(timer.isWarning());

        // Force instant expiration
        timer.expireNow();
        assertFalse(timer.isRunning());
        assertEquals(0, timer.getRemainingSeconds());
        assertTrue(timeoutFired[0]);
    }

    // ==========================================
    // 5. Automatic Quiz Submission & Duplicate Protection
    // ==========================================
    @Test
    @DisplayName("Atomic submission, preventing duplicate submissions, and AppState propagation")
    public void testQuizSubmissionAndDuplicateProtection() {
        quizService.startQuiz("AutoSubmitUser", Category.PROGRAMMING, Difficulty.MEDIUM, 5);

        // Record answers for 3 questions
        quizService.recordAnswer(0, 0);
        quizService.recordAnswer(1, 1);
        quizService.recordAnswer(2, 2);

        assertFalse(quizService.isSubmitted());

        // First submission
        QuizResult res1 = quizService.submitQuiz(appState);
        assertNotNull(res1);
        assertTrue(quizService.isSubmitted());
        assertFalse(quizService.getTimerService().isRunning()); // Timer must stop
        assertEquals(res1, appState.getCurrentResult());

        // Verify attempt added to history
        assertFalse(appState.getScoreHistory().isEmpty());
        QuizAttempt latestAttempt = appState.getScoreHistory().get(0);
        assertEquals(res1.getScore(), latestAttempt.getScore());

        int historySizeBefore = appState.getScoreHistory().size();

        // Second submission attempt (must be blocked and return same result without duplicating history)
        QuizResult res2 = quizService.submitQuiz(appState);
        assertSame(res1, res2);
        assertEquals(historySizeBefore, appState.getScoreHistory().size(), "Duplicate submission must not add duplicate history record");
    }

    @Test
    @DisplayName("Starting a new quiz resets previous state, answers, and timer")
    public void testStartingNewQuizResetsSession() {
        // Run first quiz
        quizService.startQuiz("UserOne", Category.PROGRAMMING, Difficulty.EASY, 5);
        quizService.recordAnswer(0, 2);
        quizService.submitQuiz(appState);
        assertTrue(quizService.isSubmitted());

        // Start new quiz for UserTwo
        quizService.startQuiz("UserTwo", Category.SCIENCE, Difficulty.HARD, 5);

        assertFalse(quizService.isSubmitted());
        assertEquals("UserTwo", quizService.getActivePlayerName());
        assertEquals(Category.SCIENCE, quizService.getActiveCategory());
        assertEquals(Difficulty.HARD, quizService.getActiveDifficulty());
        assertEquals(0, quizService.getCurrentQuestionIndex());
        assertNull(quizService.getUserAnswer(0), "User answers must be cleared for new quiz");
        assertEquals(0, quizService.getAnsweredCount());
        assertTrue(quizService.getTimerService().isRunning());
    }

    @Test
    @DisplayName("Insufficient questions error validation triggers clear message instead of crashing")
    public void testInsufficientQuestionsValidation() {
        assertThrows(IllegalStateException.class, () -> {
            // Request 50 questions when max available in category is 15
            quizService.startQuiz("GreedyPlayer", Category.MATHEMATICS, Difficulty.HARD, 50);
        });
    }

    // ==========================================
    // 6. Navigation and UI Component Unit Tests
    // ==========================================
    @Test
    @DisplayName("Navigation controller stack and back navigation")
    public void testNavigationStack() {
        JPanel container = new JPanel();
        CardLayout layout = new CardLayout();
        container.setLayout(layout);

        NavigationController controller = new NavigationController(container, layout, appState);
        assertEquals(NavigationController.SCREEN_HOME, controller.getCurrentScreen());

        controller.navigateTo(NavigationController.SCREEN_PLAYER_SETUP);
        assertEquals(NavigationController.SCREEN_PLAYER_SETUP, controller.getCurrentScreen());

        controller.navigateTo(NavigationController.SCREEN_QUIZ);
        assertEquals(NavigationController.SCREEN_QUIZ, controller.getCurrentScreen());

        controller.navigateBack();
        assertEquals(NavigationController.SCREEN_PLAYER_SETUP, controller.getCurrentScreen());

        controller.navigateToHome();
        assertEquals(NavigationController.SCREEN_HOME, controller.getCurrentScreen());
    }

    @Test
    @DisplayName("Custom UI Components instantiate cleanly")
    public void testCustomUIComponents() {
        RoundedPanel panel = new RoundedPanel(UIConstants.RADIUS_MD, UIConstants.SURFACE_CARD);
        assertNotNull(panel);
        panel.setSelected(true);
        assertTrue(panel.isSelected());

        RoundedButton button = new RoundedButton("Start Quiz", RoundedButton.ButtonVariant.PRIMARY);
        assertNotNull(button);
        assertEquals("Start Quiz", button.getText());

        CircularProgressIndicator indicator = new CircularProgressIndicator(85);
        assertEquals(85, indicator.getProgress());
        indicator.setProgress(50);
        assertEquals(50, indicator.getProgress());

        JTextField styledField = Theme.createStyledTextField("Test Placeholder");
        assertNotNull(styledField);
    }
}

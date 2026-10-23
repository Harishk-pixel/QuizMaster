package com.quizmaster;

import com.quizmaster.controller.NavigationController;
import com.quizmaster.model.AppState;
import com.quizmaster.model.Category;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.QuizAttempt;
import com.quizmaster.model.QuizResult;
import com.quizmaster.util.Theme;
import com.quizmaster.view.MainFrame;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuizMasterUISimulationTest {

    @Test
    @DisplayName("MainFrame initializes, navigates across all screens, simulates full quiz session, and handles resizing")
    public void testFullUINavigationAndLifecycle() throws InterruptedException, InvocationTargetException {
        // Run on Event Dispatch Thread
        SwingUtilities.invokeAndWait(() -> {
            Theme.applyGlobalTheme();

            MainFrame frame = new MainFrame();
            assertNotNull(frame);
            assertEquals("QuizMaster — Quiz Application with Score Tracking", frame.getTitle());

            // Window Dimensions
            assertEquals(1280, frame.getWidth());
            assertEquals(800, frame.getHeight());
            assertEquals(new Dimension(1000, 650), frame.getMinimumSize());

            NavigationController nav = frame.getNavigationController();
            AppState state = frame.getAppState();

            // 1. Initial Screen: HOME
            assertEquals(NavigationController.SCREEN_HOME, nav.getCurrentScreen());

            // 2. Navigate HOME -> PLAYER_SETUP
            nav.navigateTo(NavigationController.SCREEN_PLAYER_SETUP);
            assertEquals(NavigationController.SCREEN_PLAYER_SETUP, nav.getCurrentScreen());

            // Configure player details
            state.setPlayerName("Samantha Reed");
            state.setSelectedCategory(Category.COMPUTER_SCIENCE);
            state.setSelectedDifficulty(Difficulty.HARD);
            state.setSelectedQuestionCount(5);

            // Start quiz session
            state.getQuizService().startQuiz("Samantha Reed", Category.COMPUTER_SCIENCE, Difficulty.HARD, 5);

            // 3. Navigate PLAYER_SETUP -> QUIZ
            nav.navigateTo(NavigationController.SCREEN_QUIZ);
            assertEquals(NavigationController.SCREEN_QUIZ, nav.getCurrentScreen());
            assertEquals("Samantha Reed", state.getPlayerName());
            assertEquals(Category.COMPUTER_SCIENCE, state.getSelectedCategory());
            assertEquals(Difficulty.HARD, state.getSelectedDifficulty());
            assertEquals(5, state.getQuizService().getTotalQuestions());
            assertTrue(state.getQuizService().getTimerService().isRunning());

            // Answer questions in simulation
            state.getQuizService().recordAnswer(0, 1);
            state.getQuizService().recordAnswer(1, 1);
            state.getQuizService().submitQuiz(state);
            assertFalse(state.getQuizService().getTimerService().isRunning()); // Timer stopped

            // 4. Navigate QUIZ -> RESULT
            nav.navigateTo(NavigationController.SCREEN_RESULT);
            assertEquals(NavigationController.SCREEN_RESULT, nav.getCurrentScreen());
            assertNotNull(state.getCurrentResult());
            assertEquals(5, state.getCurrentResult().getTotalQuestions());

            // 5. Navigate RESULT -> ANSWER_REVIEW
            nav.navigateTo(NavigationController.SCREEN_ANSWER_REVIEW);
            assertEquals(NavigationController.SCREEN_ANSWER_REVIEW, nav.getCurrentScreen());
            assertEquals(5, state.getCurrentResult().getReviewItems().size());

            // 6. Navigate Back (ANSWER_REVIEW -> RESULT)
            nav.navigateBack();
            assertEquals(NavigationController.SCREEN_RESULT, nav.getCurrentScreen());

            // 7. Navigate RESULT -> SCORE_HISTORY
            nav.navigateTo(NavigationController.SCREEN_SCORE_HISTORY);
            assertEquals(NavigationController.SCREEN_SCORE_HISTORY, nav.getCurrentScreen());
            assertFalse(state.getScoreHistory().isEmpty());

            // 8. Navigate Back to HOME
            nav.navigateToHome();
            assertEquals(NavigationController.SCREEN_HOME, nav.getCurrentScreen());

            // 9. Verify state was preserved when returning to setup
            nav.navigateTo(NavigationController.SCREEN_PLAYER_SETUP);
            assertEquals("Samantha Reed", state.getPlayerName());
            assertEquals(Category.COMPUTER_SCIENCE, state.getSelectedCategory());
            assertEquals(Difficulty.HARD, state.getSelectedDifficulty());

            // 10. Test Window Resizing behavior
            frame.setSize(1000, 650); // Minimum size
            frame.revalidate();
            frame.repaint();
            assertEquals(1000, frame.getWidth());
            assertEquals(650, frame.getHeight());

            frame.setSize(1440, 900); // Expanded size
            frame.revalidate();
            frame.repaint();
            assertEquals(1440, frame.getWidth());
            assertEquals(900, frame.getHeight());

            frame.dispose();
        });
    }

    @Test
    @DisplayName("Complete workflow: session 1 submission, historical attempt review, and starting session 2 with fresh state")
    public void testSequentialSessionsAndHistoricalReviewWorkflow() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> {
            MainFrame frame = new MainFrame();
            NavigationController nav = frame.getNavigationController();
            AppState state = frame.getAppState();

            // 1. First Quiz Session
            state.getQuizService().startQuiz("Player One", Category.PROGRAMMING, Difficulty.EASY, 5);
            state.getQuizService().recordAnswer(0, 2);
            state.getQuizService().recordAnswer(1, 1);
            assertEquals(2, state.getQuizService().getAnsweredCount());

            QuizResult res1 = state.getQuizService().submitQuiz(state);
            assertNotNull(res1);
            assertEquals(5, res1.getTotalQuestions());

            // 2. Review attempt from Score History
            nav.navigateTo(NavigationController.SCREEN_SCORE_HISTORY);
            assertFalse(state.getScoreHistory().isEmpty());
            QuizAttempt attemptToReview = state.getScoreHistory().get(0);
            state.setSelectedHistoricalAttempt(attemptToReview);

            nav.navigateTo(NavigationController.SCREEN_ANSWER_REVIEW);
            assertEquals(NavigationController.SCREEN_ANSWER_REVIEW, nav.getCurrentScreen());

            // 3. Navigate back to Score History
            nav.navigateBack();
            assertEquals(NavigationController.SCREEN_SCORE_HISTORY, nav.getCurrentScreen());

            // 4. Start Second Quiz Session for different player
            state.getQuizService().startQuiz("Player Two", Category.SCIENCE, Difficulty.MEDIUM, 5);
            assertEquals(0, state.getQuizService().getAnsweredCount(), "Second quiz session must start with 0 answers recorded");
            assertEquals(0, state.getQuizService().getUserAnswers().size(), "Second quiz session must have clean user answers map");
            assertFalse(state.getQuizService().isSubmitted(), "Second quiz session must not be marked as submitted");
            assertEquals("Player Two", state.getQuizService().getActivePlayerName());
            assertEquals(Category.SCIENCE, state.getQuizService().getActiveCategory());

            frame.dispose();
        });
    }
}

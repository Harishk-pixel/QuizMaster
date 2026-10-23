package com.quizmaster.controller;

import com.quizmaster.model.AppState;
import java.awt.CardLayout;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

/**
 * Central controller coordinating screen navigation, view transitions, and state propagation.
 */
public class NavigationController {

    public static final String SCREEN_HOME = "HOME";
    public static final String SCREEN_PLAYER_SETUP = "PLAYER_SETUP";
    public static final String SCREEN_QUIZ = "QUIZ";
    public static final String SCREEN_RESULT = "RESULT";
    public static final String SCREEN_SCORE_HISTORY = "SCORE_HISTORY";
    public static final String SCREEN_ANSWER_REVIEW = "ANSWER_REVIEW";

    private final JPanel container;
    private final CardLayout cardLayout;
    private final AppState appState;
    private final Deque<String> navigationStack = new ArrayDeque<>();
    private final Map<String, RefreshableScreen> screens = new HashMap<>();
    private String currentScreen = SCREEN_HOME;

    /**
     * Interface for screens that need to update their UI when navigated into.
     */
    public interface RefreshableScreen {
        void onScreenShow(AppState state);
    }

    public NavigationController(JPanel container, CardLayout cardLayout, AppState appState) {
        this.container = container;
        this.cardLayout = cardLayout;
        this.appState = appState;
        this.navigationStack.push(SCREEN_HOME);
    }

    public void registerScreen(String screenName, RefreshableScreen screen) {
        screens.put(screenName, screen);
    }

    public void navigateTo(String screenName) {
        if (screenName == null || screenName.equals(currentScreen)) {
            return;
        }

        navigationStack.push(screenName);
        showScreenInternal(screenName);
    }

    public void navigateBack() {
        if (navigationStack.size() > 1) {
            navigationStack.pop(); // Remove current screen
            String previousScreen = navigationStack.peek();
            showScreenInternal(previousScreen);
        } else {
            navigateToHome();
        }
    }

    public void navigateToHome() {
        navigationStack.clear();
        navigationStack.push(SCREEN_HOME);
        showScreenInternal(SCREEN_HOME);
    }

    private void showScreenInternal(String screenName) {
        // If navigating away from an active quiz session, halt the timer safely
        if (SCREEN_QUIZ.equals(this.currentScreen) && !SCREEN_QUIZ.equals(screenName)) {
            if (appState != null && appState.getQuizService() != null) {
                appState.getQuizService().getTimerService().stop();
            }
        }

        this.currentScreen = screenName;

        // If screen is refreshable, inform it before displaying
        RefreshableScreen screen = screens.get(screenName);
        if (screen != null) {
            screen.onScreenShow(appState);
        }

        cardLayout.show(container, screenName);
        container.revalidate();
        container.repaint();
    }

    public AppState getAppState() {
        return appState;
    }

    public String getCurrentScreen() {
        return currentScreen;
    }
}

package com.quizmaster.view;

import com.quizmaster.controller.NavigationController;
import com.quizmaster.model.AppState;
import com.quizmaster.util.Theme;
import com.quizmaster.util.UIConstants;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Main application window orchestrating the CardLayout container,
 * navigation controller, and high-DPI application branding icon.
 */
public class MainFrame extends JFrame {

    private final AppState appState;
    private final CardLayout cardLayout;
    private final JPanel cardsPanel;
    private final NavigationController navigationController;

    public MainFrame() {
        super("QuizMaster — Quiz Application with Score Tracking");

        this.appState = new AppState();
        this.cardLayout = new CardLayout();
        this.cardsPanel = new JPanel(cardLayout);

        this.navigationController = new NavigationController(cardsPanel, cardLayout, appState);

        initFrame();
        initScreens();

        // Default screen is Home
        navigationController.navigateToHome();
    }

    private void initFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null); // Center on screen
        getContentPane().setBackground(UIConstants.BG_DARK);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (appState != null && appState.getQuizService() != null) {
                    appState.getQuizService().cancelQuiz();
                }
            }
        });

        // Generate and set high-resolution application icon
        setIconImage(createAppIcon());
    }

    private void initScreens() {
        // Instantiate views
        HomePanel homePanel = new HomePanel(navigationController);
        PlayerSetupPanel playerSetupPanel = new PlayerSetupPanel(navigationController);
        QuizPanel quizPanel = new QuizPanel(navigationController);
        ResultPanel resultPanel = new ResultPanel(navigationController);
        ScoreHistoryPanel scoreHistoryPanel = new ScoreHistoryPanel(navigationController);
        AnswerReviewPanel answerReviewPanel = new AnswerReviewPanel(navigationController);

        // Register with NavigationController for lifecycle state notifications
        navigationController.registerScreen(NavigationController.SCREEN_HOME, homePanel);
        navigationController.registerScreen(NavigationController.SCREEN_PLAYER_SETUP, playerSetupPanel);
        navigationController.registerScreen(NavigationController.SCREEN_QUIZ, quizPanel);
        navigationController.registerScreen(NavigationController.SCREEN_RESULT, resultPanel);
        navigationController.registerScreen(NavigationController.SCREEN_SCORE_HISTORY, scoreHistoryPanel);
        navigationController.registerScreen(NavigationController.SCREEN_ANSWER_REVIEW, answerReviewPanel);

        // Add to CardLayout container
        cardsPanel.add(homePanel, NavigationController.SCREEN_HOME);
        cardsPanel.add(playerSetupPanel, NavigationController.SCREEN_PLAYER_SETUP);
        cardsPanel.add(quizPanel, NavigationController.SCREEN_QUIZ);
        cardsPanel.add(resultPanel, NavigationController.SCREEN_RESULT);
        cardsPanel.add(scoreHistoryPanel, NavigationController.SCREEN_SCORE_HISTORY);
        cardsPanel.add(answerReviewPanel, NavigationController.SCREEN_ANSWER_REVIEW);

        getContentPane().add(cardsPanel);
    }

    private Image createAppIcon() {
        int size = 64;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Rounded gradient purple background
        g2.setColor(UIConstants.ACCENT_PRIMARY);
        g2.fillRoundRect(2, 2, size - 4, size - 4, 18, 18);

        // Subtle accent dot
        g2.setColor(UIConstants.ACCENT_SECONDARY);
        g2.fillOval(size - 18, 8, 10, 10);

        // Distinct White "Q"
        g2.setColor(UIConstants.TEXT_MAIN);
        g2.setFont(UIConstants.getFont(java.awt.Font.BOLD, 38f));
        g2.drawString("Q", 16, 46);

        g2.dispose();
        return image;
    }

    public NavigationController getNavigationController() {
        return navigationController;
    }

    public AppState getAppState() {
        return appState;
    }
}

package com.quizmaster.view;

import com.quizmaster.controller.NavigationController;
import com.quizmaster.model.AppState;
import com.quizmaster.model.QuizResult;
import com.quizmaster.util.CircularProgressIndicator;
import com.quizmaster.util.CustomScrollBarUI;
import com.quizmaster.util.RoundedButton;
import com.quizmaster.util.RoundedPanel;
import com.quizmaster.util.Theme;
import com.quizmaster.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Result screen displaying final score, percentage ring, time taken,
 * correct/incorrect breakdown, database persistence status, and navigation actions.
 */
public class ResultPanel extends JPanel implements NavigationController.RefreshableScreen {

    private final NavigationController navigationController;

    private JLabel headingLabel;
    private JLabel detailsSubtitle;
    private JLabel feedbackText;
    private JLabel persistenceBadge;
    private RoundedButton retrySaveBtn;
    private CircularProgressIndicator progressIndicator;

    private JLabel scoreValLabel;
    private JLabel percentValLabel;
    private JLabel timeValLabel;
    private JLabel correctValLabel;
    private JLabel incorrectValLabel;
    private JLabel unansweredValLabel;

    public ResultPanel(NavigationController navigationController) {
        this.navigationController = navigationController;
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_DARK);

        initUI();
    }

    private void initUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UIConstants.BG_DARK);
        content.setBorder(BorderFactory.createEmptyBorder(28, 48, 32, 48));

        // Header Title & Persistence Badge
        content.add(createHeaderSection());
        content.add(Box.createVerticalStrut(24));

        // Central Score Showcase Card (Circular Progress + Metric Cards)
        content.add(createScoreShowcase());
        content.add(Box.createVerticalStrut(24));

        // Secondary Metrics Grid (Correct, Incorrect, Unanswered, Total)
        content.add(createSecondaryMetricsRow());
        content.add(Box.createVerticalStrut(32));

        // Action Buttons Row
        content.add(createActionsRow());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getViewport().setBackground(UIConstants.BG_DARK);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(1920, 80));

        JPanel topBadges = new JPanel(new BorderLayout());
        topBadges.setOpaque(false);

        JLabel completeBadge = Theme.createBadge("ASSESSMENT COMPLETED", UIConstants.SUCCESS_BG, UIConstants.SUCCESS);

        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightGroup.setOpaque(false);

        persistenceBadge = Theme.createBadge("💾 Database Syncing...", UIConstants.SURFACE_SECONDARY, UIConstants.TEXT_MUTED);

        retrySaveBtn = new RoundedButton("Retry Save", RoundedButton.ButtonVariant.OUTLINE);
        retrySaveBtn.setPreferredSize(new Dimension(110, 28));
        retrySaveBtn.setFont(UIConstants.FONT_BADGE);
        retrySaveBtn.setVisible(false);
        retrySaveBtn.addActionListener(e -> handleRetrySave());

        rightGroup.add(persistenceBadge);
        rightGroup.add(retrySaveBtn);

        topBadges.add(completeBadge, BorderLayout.WEST);
        topBadges.add(rightGroup, BorderLayout.EAST);

        headingLabel = new JLabel("Quiz Completed!");
        headingLabel.setFont(UIConstants.FONT_HERO);
        headingLabel.setForeground(UIConstants.TEXT_MAIN);

        detailsSubtitle = new JLabel("Performance overview for Player");
        detailsSubtitle.setFont(UIConstants.FONT_SUBTITLE);
        detailsSubtitle.setForeground(UIConstants.TEXT_MUTED);

        panel.add(topBadges);
        panel.add(Box.createVerticalStrut(8));
        panel.add(headingLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(detailsSubtitle);

        return panel;
    }

    private JPanel createScoreShowcase() {
        RoundedPanel card = new RoundedPanel(UIConstants.RADIUS_LG, UIConstants.SURFACE_CARD, UIConstants.BORDER_SUBTLE, 1);
        card.setLayout(new BorderLayout(32, 0));
        card.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));
        card.setMaximumSize(new Dimension(1920, 240));

        // Left: Circular Progress Ring
        JPanel circleWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        circleWrapper.setOpaque(false);
        progressIndicator = new CircularProgressIndicator(80);
        progressIndicator.setSubtitle("Accuracy");
        circleWrapper.add(progressIndicator);

        // Right: Score Overview and Feedback
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);

        JLabel scoreSummaryTitle = new JLabel("Performance Summary");
        scoreSummaryTitle.setFont(UIConstants.FONT_H2);
        scoreSummaryTitle.setForeground(UIConstants.TEXT_MAIN);

        feedbackText = new JLabel("Loading performance feedback...");
        feedbackText.setFont(UIConstants.FONT_BODY);
        feedbackText.setForeground(UIConstants.TEXT_MUTED);
        feedbackText.setPreferredSize(new Dimension(500, 48));

        // Quick Highlights Pill Row
        JPanel pillsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        pillsRow.setOpaque(false);
        pillsRow.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        scoreValLabel = new JLabel("Score: 0 / 0");
        scoreValLabel.setFont(UIConstants.FONT_H3);
        scoreValLabel.setForeground(UIConstants.ACCENT_PRIMARY);

        percentValLabel = new JLabel("• Percentage: 0%");
        percentValLabel.setFont(UIConstants.FONT_H3);
        percentValLabel.setForeground(UIConstants.ACCENT_SECONDARY);

        timeValLabel = new JLabel("• Time Taken: 00:00");
        timeValLabel.setFont(UIConstants.FONT_H3);
        timeValLabel.setForeground(UIConstants.SUCCESS);

        pillsRow.add(scoreValLabel);
        pillsRow.add(percentValLabel);
        pillsRow.add(timeValLabel);

        statsPanel.add(scoreSummaryTitle);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(feedbackText);
        statsPanel.add(Box.createVerticalStrut(16));
        statsPanel.add(pillsRow);

        card.add(circleWrapper, BorderLayout.WEST);
        card.add(statsPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createSecondaryMetricsRow() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(1920, 110));

        // Metric 1: Correct Answers
        correctValLabel = new JLabel("0");
        grid.add(createMetricCard("CORRECT ANSWERS", correctValLabel, "Accurate answers", UIConstants.SUCCESS, UIConstants.SUCCESS_BG));

        // Metric 2: Incorrect Answers
        incorrectValLabel = new JLabel("0");
        grid.add(createMetricCard("INCORRECT ANSWERS", incorrectValLabel, "Incorrect selections", UIConstants.ERROR, UIConstants.ERROR_BG));

        // Metric 3: Unanswered
        unansweredValLabel = new JLabel("0");
        grid.add(createMetricCard("UNANSWERED", unansweredValLabel, "Skipped questions", UIConstants.TEXT_MUTED, UIConstants.SURFACE_SECONDARY));

        // Metric 4: Total Questions
        JLabel totalCountLabel = new JLabel("0");
        grid.add(createMetricCard("TOTAL QUESTIONS", totalCountLabel, "Presented questions", UIConstants.ACCENT_SECONDARY, UIConstants.ACCENT_SECONDARY_LIGHT));

        return grid;
    }

    private RoundedPanel createMetricCard(String tag, JLabel valueLabel, String subtitle, Color accentColor, Color bgTint) {
        RoundedPanel card = new RoundedPanel(UIConstants.RADIUS_MD, UIConstants.SURFACE_CARD, UIConstants.BORDER_SUBTLE, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel tagLabel = new JLabel(tag);
        tagLabel.setFont(UIConstants.FONT_BADGE);
        tagLabel.setForeground(accentColor);

        valueLabel.setFont(UIConstants.getFont(java.awt.Font.BOLD, 26f));
        valueLabel.setForeground(UIConstants.TEXT_MAIN);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(UIConstants.FONT_CAPTION);
        subLabel.setForeground(UIConstants.TEXT_MUTED);

        card.add(tagLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subLabel);

        return card;
    }

    private JPanel createActionsRow() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        actions.setOpaque(false);
        actions.setMaximumSize(new Dimension(1920, 50));

        RoundedButton playAgainBtn = new RoundedButton("Start Another Quiz", RoundedButton.ButtonVariant.PRIMARY);
        playAgainBtn.setPreferredSize(new Dimension(170, 44));
        playAgainBtn.addActionListener(e -> navigationController.navigateTo(NavigationController.SCREEN_PLAYER_SETUP));

        RoundedButton reviewBtn = new RoundedButton("Review Answers", RoundedButton.ButtonVariant.SECONDARY);
        reviewBtn.setPreferredSize(new Dimension(160, 44));
        reviewBtn.addActionListener(e -> {
            // Ensure review is inspecting this current result
            AppState state = navigationController.getAppState();
            state.setSelectedHistoricalAttempt(null);
            navigationController.navigateTo(NavigationController.SCREEN_ANSWER_REVIEW);
        });

        RoundedButton historyBtn = new RoundedButton("View Score History", RoundedButton.ButtonVariant.OUTLINE);
        historyBtn.setPreferredSize(new Dimension(170, 44));
        historyBtn.addActionListener(e -> navigationController.navigateTo(NavigationController.SCREEN_SCORE_HISTORY));

        RoundedButton homeBtn = new RoundedButton("Back to Home", RoundedButton.ButtonVariant.SUBTLE);
        homeBtn.setPreferredSize(new Dimension(140, 44));
        homeBtn.addActionListener(e -> navigationController.navigateToHome());

        actions.add(playAgainBtn);
        actions.add(reviewBtn);
        actions.add(historyBtn);
        actions.add(homeBtn);

        return actions;
    }

    private void handleRetrySave() {
        AppState state = navigationController.getAppState();
        boolean saved = state.getQuizService().retryPersistence(state);

        if (saved) {
            updatePersistenceDisplay(state.getCurrentResult());
            JOptionPane.showMessageDialog(
                    this,
                    "Quiz attempt saved to MySQL database successfully!",
                    "Database Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to connect to MySQL database.\nPlease verify that your MySQL server is running and QUIZ_DB_URL is configured.",
                    "Database Unavailable",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void updatePersistenceDisplay(QuizResult res) {
        if (res.isPersisted()) {
            persistenceBadge.setText("💾 Saved to MySQL (Attempt #QM-" + res.getDatabaseAttemptId() + ")");
            persistenceBadge.setForeground(UIConstants.SUCCESS);
            retrySaveBtn.setVisible(false);
        } else {
            persistenceBadge.setText("⚠ Offline Mode (Preserved in memory only)");
            persistenceBadge.setForeground(UIConstants.WARNING);
            retrySaveBtn.setVisible(true);
        }
        persistenceBadge.repaint();
    }

    @Override
    public void onScreenShow(AppState state) {
        QuizResult res = state.getCurrentResult();
        if (res == null) {
            state.generateDemoResult();
            res = state.getCurrentResult();
        }

        String name = res.getPlayerName();
        String catName = res.getCategory() != null ? res.getCategory().getDisplayName() : "General";
        String diffName = res.getDifficulty() != null ? res.getDifficulty().getDisplayName() : "Medium";

        detailsSubtitle.setText("Assessment summary for " + name + " • " + catName + " (" + diffName + ")");

        progressIndicator.setProgress(res.getPercentage());
        feedbackText.setText("<html>" + res.getPerformanceMessage() + "</html>");

        scoreValLabel.setText("Score: " + res.getScore() + " / " + res.getMaxScore());
        percentValLabel.setText("• Percentage: " + res.getPercentage() + "%");
        timeValLabel.setText("• Time Taken: " + res.getTimeTakenFormatted());

        correctValLabel.setText(String.valueOf(res.getCorrectCount()));
        incorrectValLabel.setText(String.valueOf(res.getIncorrectCount()));
        unansweredValLabel.setText(String.valueOf(res.getUnansweredCount()));

        updatePersistenceDisplay(res);
    }
}

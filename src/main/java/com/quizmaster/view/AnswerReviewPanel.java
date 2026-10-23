package com.quizmaster.view;

import com.quizmaster.controller.NavigationController;
import com.quizmaster.model.AppState;
import com.quizmaster.model.Question;
import com.quizmaster.model.QuizAttempt;
import com.quizmaster.model.QuizResult;
import com.quizmaster.util.CustomScrollBarUI;
import com.quizmaster.util.RoundedButton;
import com.quizmaster.util.RoundedPanel;
import com.quizmaster.util.Theme;
import com.quizmaster.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Answer review screen displaying detailed question-by-question breakdowns,
 * all four options with highlighted selections and correct indicators,
 * status badges, and educational explanations.
 */
public class AnswerReviewPanel extends JPanel implements NavigationController.RefreshableScreen {

    private final NavigationController navigationController;

    private JLabel subtitleLabel;
    private JLabel scoreSummaryBadge;
    private JPanel reviewItemsList;
    private RoundedButton topBackBtn;
    private RoundedButton bottomBackBtn;
    private boolean isHistoricalReview = false;

    public AnswerReviewPanel(NavigationController navigationController) {
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

        // Top Header
        content.add(createHeader());
        content.add(Box.createVerticalStrut(24));

        // Review Items Container
        reviewItemsList = new JPanel();
        reviewItemsList.setLayout(new BoxLayout(reviewItemsList, BoxLayout.Y_AXIS));
        reviewItemsList.setOpaque(false);

        content.add(reviewItemsList);
        content.add(Box.createVerticalStrut(32));

        // Bottom Navigation Bar
        content.add(createBottomBar());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getViewport().setBackground(UIConstants.BG_DARK);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(1920, 50));

        JPanel textGroup = new JPanel();
        textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));
        textGroup.setOpaque(false);

        JLabel title = new JLabel("Answer Review");
        title.setFont(UIConstants.FONT_H1);
        title.setForeground(UIConstants.TEXT_MAIN);

        subtitleLabel = new JLabel("Detailed performance breakdown");
        subtitleLabel.setFont(UIConstants.FONT_SUBTITLE);
        subtitleLabel.setForeground(UIConstants.TEXT_MUTED);

        textGroup.add(title);
        textGroup.add(subtitleLabel);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightActions.setOpaque(false);

        scoreSummaryBadge = Theme.createBadge("Score: 0 / 0", UIConstants.SUCCESS_BG, UIConstants.SUCCESS);

        topBackBtn = new RoundedButton("← Back to Results", RoundedButton.ButtonVariant.OUTLINE);
        topBackBtn.setPreferredSize(new Dimension(160, 36));
        topBackBtn.addActionListener(e -> handleBackNavigation());

        rightActions.add(scoreSummaryBadge);
        rightActions.add(topBackBtn);

        header.add(textGroup, BorderLayout.WEST);
        header.add(rightActions, BorderLayout.EAST);

        return header;
    }

    private void renderReviewItems(List<QuizResult.ReviewItem> items) {
        reviewItemsList.removeAll();

        if (items == null || items.isEmpty()) {
            JLabel emptyLabel = new JLabel("No review items available.");
            emptyLabel.setFont(UIConstants.FONT_BODY);
            emptyLabel.setForeground(UIConstants.TEXT_MUTED);
            reviewItemsList.add(emptyLabel);
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            QuizResult.ReviewItem item = items.get(i);
            reviewItemsList.add(createQuestionReviewCard(item, i + 1));
            if (i < items.size() - 1) {
                reviewItemsList.add(Box.createVerticalStrut(20));
            }
        }

        reviewItemsList.revalidate();
        reviewItemsList.repaint();
    }

    private RoundedPanel createQuestionReviewCard(QuizResult.ReviewItem item, int questionNumber) {
        Question q = item.getQuestion();
        QuizResult.AnswerStatus status = item.getStatus();

        Color cardBorder;
        JLabel statusBadge;

        if (status == QuizResult.AnswerStatus.CORRECT) {
            cardBorder = new Color(0x35, 0xC9, 0x8A, 70);
            statusBadge = Theme.createBadge("✓ CORRECT (+1)", UIConstants.SUCCESS_BG, UIConstants.SUCCESS);
        } else if (status == QuizResult.AnswerStatus.INCORRECT) {
            cardBorder = new Color(0xFF, 0x64, 0x7C, 70);
            statusBadge = Theme.createBadge("✕ INCORRECT (0)", UIConstants.ERROR_BG, UIConstants.ERROR);
        } else {
            cardBorder = new Color(0x9A, 0xA8, 0xBD, 40);
            statusBadge = Theme.createBadge("⊘ UNANSWERED (0)", new Color(0x22, 0x30, 0x48), UIConstants.TEXT_MUTED);
        }

        RoundedPanel card = new RoundedPanel(UIConstants.RADIUS_MD, UIConstants.SURFACE_CARD, cardBorder, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        card.setMaximumSize(new Dimension(1920, 380));

        // Top Status Header (Question Number + Badge)
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel qNum = new JLabel("Question " + questionNumber);
        qNum.setFont(UIConstants.FONT_H3);
        qNum.setForeground(UIConstants.TEXT_MAIN);

        topRow.add(qNum, BorderLayout.WEST);
        topRow.add(statusBadge, BorderLayout.EAST);

        // Question Text
        JLabel qText = new JLabel("<html>" + q.getQuestionText() + "</html>");
        qText.setFont(UIConstants.FONT_BODY_BOLD);
        qText.setForeground(UIConstants.TEXT_MAIN);

        // Options List (Showing all 4 options with contextual highlight)
        JPanel optionsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsGrid.setOpaque(false);
        optionsGrid.setMaximumSize(new Dimension(1920, 100));

        char optLetter = 'A';
        List<String> options = q.getOptions();
        for (int i = 0; i < options.size(); i++) {
            String optText = options.get(i);
            boolean isCorrectOption = (i == q.getCorrectOptionIndex());
            boolean isSelectedOption = (i == item.getSelectedOptionIndex());

            Color optBg = UIConstants.SURFACE_SECONDARY;
            Color optStroke = UIConstants.BORDER_SUBTLE;
            Color textFg = UIConstants.TEXT_MUTED;
            String tag = "";

            if (isCorrectOption && isSelectedOption) {
                optBg = new Color(0x16, 0x38, 0x2E);
                optStroke = UIConstants.SUCCESS;
                textFg = UIConstants.SUCCESS;
                tag = " (Your Pick ✓)";
            } else if (isCorrectOption) {
                optBg = new Color(0x16, 0x38, 0x2E);
                optStroke = UIConstants.SUCCESS;
                textFg = UIConstants.SUCCESS;
                tag = " (Correct Answer)";
            } else if (isSelectedOption) {
                optBg = new Color(0x3B, 0x1A, 0x24);
                optStroke = UIConstants.ERROR;
                textFg = UIConstants.ERROR;
                tag = " (Your Pick ✕)";
            }

            RoundedPanel optPanel = new RoundedPanel(UIConstants.RADIUS_SM, optBg, optStroke, 1);
            optPanel.setLayout(new BorderLayout(8, 0));
            optPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            JLabel optLabel = new JLabel("<html><b>" + optLetter + ")</b> " + optText + "<font color='"
                    + (isCorrectOption ? "#35C98A" : "#FF647C") + "'>" + tag + "</font></html>");
            optLabel.setFont(UIConstants.FONT_CAPTION);
            optLabel.setForeground(textFg);

            optPanel.add(optLabel, BorderLayout.CENTER);
            optionsGrid.add(optPanel);
            optLetter++;
        }

        // Summary selection row
        JPanel summaryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        summaryRow.setOpaque(false);
        summaryRow.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JLabel choiceLabel = new JLabel("Player's Pick: " + item.getSelectedAnswerText());
        choiceLabel.setFont(UIConstants.FONT_BODY);
        choiceLabel.setForeground(status == QuizResult.AnswerStatus.CORRECT ? UIConstants.SUCCESS : (status == QuizResult.AnswerStatus.INCORRECT ? UIConstants.ERROR : UIConstants.TEXT_MUTED));

        summaryRow.add(choiceLabel);

        // Explanation Box
        RoundedPanel explanationBox = new RoundedPanel(UIConstants.RADIUS_SM, UIConstants.SURFACE_SECONDARY, UIConstants.BORDER_SUBTLE, 1);
        explanationBox.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
        explanationBox.setMaximumSize(new Dimension(1920, 60));

        JLabel expIcon = new JLabel("💡");
        expIcon.setFont(UIConstants.getFont(Font.PLAIN, 14f));

        JLabel expText = new JLabel("<html><b>Explanation:</b> " + item.getExplanation() + "</html>");
        expText.setFont(UIConstants.FONT_CAPTION);
        expText.setForeground(UIConstants.TEXT_MUTED);

        explanationBox.add(expIcon);
        explanationBox.add(expText);

        card.add(topRow);
        card.add(Box.createVerticalStrut(10));
        card.add(qText);
        card.add(Box.createVerticalStrut(12));
        card.add(optionsGrid);
        card.add(Box.createVerticalStrut(8));
        card.add(summaryRow);
        card.add(Box.createVerticalStrut(10));
        card.add(explanationBox);

        return card;
    }

    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setMaximumSize(new Dimension(1920, 50));

        RoundedButton homeBtn = new RoundedButton("Back to Home", RoundedButton.ButtonVariant.OUTLINE);
        homeBtn.setPreferredSize(new Dimension(140, 42));
        homeBtn.addActionListener(e -> {
            navigationController.getAppState().setSelectedHistoricalAttempt(null);
            navigationController.navigateToHome();
        });

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightActions.setOpaque(false);

        RoundedButton historyBtn = new RoundedButton("View Score History", RoundedButton.ButtonVariant.SUBTLE);
        historyBtn.setPreferredSize(new Dimension(160, 42));
        historyBtn.addActionListener(e -> {
            navigationController.getAppState().setSelectedHistoricalAttempt(null);
            navigationController.navigateTo(NavigationController.SCREEN_SCORE_HISTORY);
        });

        bottomBackBtn = new RoundedButton("Back to Results", RoundedButton.ButtonVariant.PRIMARY);
        bottomBackBtn.setPreferredSize(new Dimension(150, 42));
        bottomBackBtn.addActionListener(e -> handleBackNavigation());

        rightActions.add(historyBtn);
        rightActions.add(bottomBackBtn);

        bar.add(homeBtn, BorderLayout.WEST);
        bar.add(rightActions, BorderLayout.EAST);

        return bar;
    }

    private void handleBackNavigation() {
        AppState state = navigationController.getAppState();
        if (isHistoricalReview) {
            state.setSelectedHistoricalAttempt(null);
            navigationController.navigateTo(NavigationController.SCREEN_SCORE_HISTORY);
        } else {
            navigationController.navigateTo(NavigationController.SCREEN_RESULT);
        }
    }

    @Override
    public void onScreenShow(AppState state) {
        QuizAttempt historical = state.getSelectedHistoricalAttempt();
        if (historical != null) {
            isHistoricalReview = true;
            topBackBtn.setText("← Back to History");
            bottomBackBtn.setText("Back to History");

            String pName = historical.getPlayerName() != null ? historical.getPlayerName() : "Player";
            String catName = historical.getCategory() != null ? historical.getCategory().getDisplayName() : "All Categories";
            String diffName = historical.getDifficulty() != null ? historical.getDifficulty().getDisplayName() : "Standard";
            int dbId = historical.getDbId();
            subtitleLabel.setText("Historical Attempt #" + (dbId > 0 ? dbId : historical.getAttemptId())
                    + " • " + pName + " • " + catName + " (" + diffName + ")");
            scoreSummaryBadge.setText("Score: " + historical.getScore() + " / " + historical.getMaxScore()
                    + " (" + historical.getPercentage() + "%)");

            List<QuizResult.ReviewItem> items = null;
            if (dbId > 0) {
                try {
                    items = state.getPersistenceService().loadHistoricalReviewItems(dbId);
                } catch (Exception ignored) {
                }
            }
            if ((items == null || items.isEmpty()) && state.getCurrentResult() != null) {
                if (state.getCurrentResult().getScore() == historical.getScore()) {
                    items = state.getCurrentResult().getReviewItems();
                }
            }

            renderReviewItems(items);
        } else {
            isHistoricalReview = false;
            topBackBtn.setText("← Back to Results");
            bottomBackBtn.setText("Back to Results");

            QuizResult res = state.getCurrentResult();
            if (res == null) {
                state.generateDemoResult();
                res = state.getCurrentResult();
            }

            String name = res.getPlayerName();
            String catName = res.getCategory() != null ? res.getCategory().getDisplayName() : "General";
            subtitleLabel.setText("Detailed performance breakdown for " + name + " • " + catName + " (" + res.getDifficulty().getDisplayName() + ")");
            scoreSummaryBadge.setText("Score: " + res.getScore() + " / " + res.getMaxScore() + " (" + res.getPercentage() + "%)");

            renderReviewItems(res.getReviewItems());
        }
    }
}

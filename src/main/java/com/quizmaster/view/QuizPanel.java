package com.quizmaster.view;

import com.quizmaster.controller.NavigationController;
import com.quizmaster.model.AppState;
import com.quizmaster.model.Category;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.service.QuizService;
import com.quizmaster.service.QuizTimerService;
import com.quizmaster.util.CustomScrollBarUI;
import com.quizmaster.util.RoundedButton;
import com.quizmaster.util.RoundedPanel;
import com.quizmaster.util.Theme;
import com.quizmaster.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Functional Quiz screen featuring interactive multiple-choice question cards,
 * per-question answer preservation, progress tracking, warning-enabled countdown timer,
 * and robust submission validation.
 */
public class QuizPanel extends JPanel implements NavigationController.RefreshableScreen {

    private final NavigationController navigationController;

    private JLabel categoryBadge;
    private JLabel difficultyBadge;
    private JLabel playerIndicator;
    private JLabel questionNumberLabel;
    private JLabel timerBadge;
    private JProgressBar progressBar;

    private JLabel questionTextLabel;
    private JPanel optionsContainer;
    private ButtonGroup optionButtonGroup;
    private final List<JRadioButton> radioButtons = new ArrayList<>();
    private final List<RoundedPanel> optionPanels = new ArrayList<>();

    private RoundedButton prevBtn;
    private RoundedButton nextBtn;
    private RoundedButton submitBtn;

    private boolean timerWarningActive = false;
    private String currentTimerText = "00:00";

    public QuizPanel(NavigationController navigationController) {
        this.navigationController = navigationController;
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_DARK);

        initUI();
    }

    private void initUI() {
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(UIConstants.BG_DARK);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 48, 24, 48));

        // Top Header with branding, category/difficulty pills, and Exit Quiz action
        mainContainer.add(createHeaderBar());
        mainContainer.add(Box.createVerticalStrut(16));

        // Status Row (Question Number & Timer)
        mainContainer.add(createStatusRow());
        mainContainer.add(Box.createVerticalStrut(12));
        mainContainer.add(createProgressBar());
        mainContainer.add(Box.createVerticalStrut(24));

        // Question Card
        mainContainer.add(createQuestionCard());
        mainContainer.add(Box.createVerticalStrut(24));

        // Bottom Controls (Prev, Next, Submit)
        mainContainer.add(createControlsRow());

        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getViewport().setBackground(UIConstants.BG_DARK);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(1920, 48));

        // Left: Branding & Player
        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftGroup.setOpaque(false);

        JLabel logoLabel = new JLabel("QuizMaster");
        logoLabel.setFont(UIConstants.FONT_LOGO);
        logoLabel.setForeground(UIConstants.TEXT_MAIN);

        playerIndicator = new JLabel("• Player: Player 1");
        playerIndicator.setFont(UIConstants.FONT_CAPTION);
        playerIndicator.setForeground(UIConstants.TEXT_MUTED);

        leftGroup.add(logoLabel);
        leftGroup.add(playerIndicator);

        // Center / Right: Category & Difficulty badges + Exit Quiz
        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightGroup.setOpaque(false);

        categoryBadge = Theme.createBadge("Category: Programming", UIConstants.ACCENT_PRIMARY_LIGHT, UIConstants.ACCENT_PRIMARY);
        difficultyBadge = Theme.createBadge("Medium", UIConstants.WARNING_BG, UIConstants.WARNING);

        RoundedButton exitBtn = new RoundedButton("✕ Exit Quiz", RoundedButton.ButtonVariant.DANGER);
        exitBtn.setPreferredSize(new Dimension(110, 34));
        exitBtn.setFont(UIConstants.FONT_CAPTION);
        exitBtn.addActionListener(e -> handleExit());

        rightGroup.add(categoryBadge);
        rightGroup.add(difficultyBadge);
        rightGroup.add(exitBtn);

        header.add(leftGroup, BorderLayout.WEST);
        header.add(rightGroup, BorderLayout.EAST);

        return header;
    }

    private JPanel createStatusRow() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(1920, 36));

        questionNumberLabel = new JLabel("Question 1 of 5");
        questionNumberLabel.setFont(UIConstants.FONT_H3);
        questionNumberLabel.setForeground(UIConstants.TEXT_MAIN);

        // Timer Display Area with custom paint
        timerBadge = new JLabel("⏱ 02:30 Remaining", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = timerWarningActive ? new Color(0x3D, 0x16, 0x22) : new Color(0x20, 0x1A, 0x36);
                Color border = timerWarningActive ? UIConstants.ERROR : UIConstants.ACCENT_PRIMARY;

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        timerBadge.setFont(UIConstants.FONT_BODY_BOLD);
        timerBadge.setForeground(UIConstants.TEXT_MAIN);
        timerBadge.setOpaque(false);
        timerBadge.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        row.add(questionNumberLabel, BorderLayout.WEST);
        row.add(timerBadge, BorderLayout.EAST);

        return row;
    }

    private JProgressBar createProgressBar() {
        progressBar = new JProgressBar(0, 10);
        progressBar.setValue(1);
        progressBar.setPreferredSize(new Dimension(1920, 8));
        progressBar.setMaximumSize(new Dimension(1920, 8));
        progressBar.setForeground(UIConstants.ACCENT_PRIMARY);
        progressBar.setBackground(UIConstants.SURFACE_SECONDARY);
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        return progressBar;
    }

    private JPanel createQuestionCard() {
        RoundedPanel card = new RoundedPanel(UIConstants.RADIUS_LG, UIConstants.SURFACE_CARD, UIConstants.BORDER_SUBTLE, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        questionTextLabel = new JLabel("Loading question...");
        questionTextLabel.setFont(UIConstants.FONT_H2);
        questionTextLabel.setForeground(UIConstants.TEXT_MAIN);

        optionsContainer = new JPanel();
        optionsContainer.setLayout(new BoxLayout(optionsContainer, BoxLayout.Y_AXIS));
        optionsContainer.setOpaque(false);

        card.add(questionTextLabel);
        card.add(Box.createVerticalStrut(24));
        card.add(optionsContainer);

        return card;
    }

    private void renderCurrentQuestion() {
        AppState state = navigationController.getAppState();
        QuizService quizService = state.getQuizService();
        Question q = quizService.getCurrentQuestion();
        if (q == null) return;

        int currentIndex = quizService.getCurrentQuestionIndex();
        int total = quizService.getTotalQuestions();

        questionNumberLabel.setText("Question " + (currentIndex + 1) + " of " + total);
        progressBar.setMaximum(total);
        progressBar.setValue(currentIndex + 1);

        questionTextLabel.setText("<html>" + q.getQuestionText() + "</html>");

        optionsContainer.removeAll();
        optionPanels.clear();
        radioButtons.clear();
        optionButtonGroup = new ButtonGroup();

        char optLetter = 'A';
        List<String> options = q.getOptions();
        for (int i = 0; i < options.size(); i++) {
            final int optionIndex = i;
            String text = options.get(i);

            RoundedPanel optCard = new RoundedPanel(UIConstants.RADIUS_MD, UIConstants.SURFACE_SECONDARY, UIConstants.BORDER_SUBTLE, 1);
            optCard.setLayout(new BorderLayout(16, 0));
            optCard.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
            optCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            optCard.setMaximumSize(new Dimension(1920, 60));

            JRadioButton radio = new JRadioButton();
            radio.setOpaque(false);
            radio.setFocusPainted(false);
            radio.setForeground(UIConstants.TEXT_MAIN);

            JLabel label = new JLabel(optLetter + ")  " + text);
            label.setFont(UIConstants.FONT_BODY);
            label.setForeground(UIConstants.TEXT_MAIN);

            optCard.add(radio, BorderLayout.WEST);
            optCard.add(label, BorderLayout.CENTER);

            optionButtonGroup.add(radio);
            radioButtons.add(radio);
            optionPanels.add(optCard);

            // Card click & radio action
            optCard.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectOption(optionIndex);
                }
            });

            radio.addActionListener(e -> selectOption(optionIndex));

            optionsContainer.add(optCard);
            if (i < options.size() - 1) {
                optionsContainer.add(Box.createVerticalStrut(12));
            }

            optLetter++;
        }

        // Restore previously selected answer if present
        Integer savedChoice = quizService.getUserAnswer(currentIndex);
        if (savedChoice != null && savedChoice >= 0 && savedChoice < radioButtons.size()) {
            selectOption(savedChoice);
        }

        updateNavigationButtons(currentIndex, total);
        optionsContainer.revalidate();
        optionsContainer.repaint();
    }

    private void selectOption(int optionIndex) {
        if (optionIndex < 0 || optionIndex >= radioButtons.size()) return;

        radioButtons.get(optionIndex).setSelected(true);

        for (int i = 0; i < optionPanels.size(); i++) {
            RoundedPanel p = optionPanels.get(i);
            boolean isSelected = (i == optionIndex);
            p.setBackgroundColor(isSelected ? UIConstants.SURFACE_ELEVATED : UIConstants.SURFACE_SECONDARY);
            p.setBorderColor(isSelected ? UIConstants.ACCENT_PRIMARY : UIConstants.BORDER_SUBTLE);
            p.repaint();
        }

        // Persist immediately in QuizService
        AppState state = navigationController.getAppState();
        state.getQuizService().recordAnswer(state.getQuizService().getCurrentQuestionIndex(), optionIndex);
    }

    private JPanel createControlsRow() {
        JPanel controls = new JPanel(new BorderLayout());
        controls.setOpaque(false);
        controls.setMaximumSize(new Dimension(1920, 48));

        prevBtn = new RoundedButton("← Previous Question", RoundedButton.ButtonVariant.OUTLINE);
        prevBtn.setPreferredSize(new Dimension(170, 42));
        prevBtn.addActionListener(e -> handlePrevious());

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightActions.setOpaque(false);

        nextBtn = new RoundedButton("Next Question →", RoundedButton.ButtonVariant.SECONDARY);
        nextBtn.setPreferredSize(new Dimension(150, 42));
        nextBtn.addActionListener(e -> handleNext());

        submitBtn = new RoundedButton("Submit Quiz ✓", RoundedButton.ButtonVariant.PRIMARY);
        submitBtn.setPreferredSize(new Dimension(160, 42));
        submitBtn.addActionListener(e -> handleSubmit(false));

        rightActions.add(nextBtn);
        rightActions.add(submitBtn);

        controls.add(prevBtn, BorderLayout.WEST);
        controls.add(rightActions, BorderLayout.EAST);

        return controls;
    }

    private void updateNavigationButtons(int currentIndex, int total) {
        prevBtn.setEnabled(currentIndex > 0);

        boolean isLast = (currentIndex == total - 1);
        nextBtn.setVisible(!isLast);
        submitBtn.setVisible(isLast);
    }

    private void handlePrevious() {
        AppState state = navigationController.getAppState();
        if (state.getQuizService().previousQuestion()) {
            renderCurrentQuestion();
        }
    }

    private void handleNext() {
        AppState state = navigationController.getAppState();
        QuizService quizService = state.getQuizService();

        // Check if player has selected an answer
        if (!quizService.isCurrentQuestionAnswered()) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "You have not selected an answer for this question.\nDo you want to proceed without answering?",
                    "Unanswered Question",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) {
                return; // User chooses to stay
            }
        }

        if (quizService.nextQuestion()) {
            renderCurrentQuestion();
        }
    }

    private synchronized void handleSubmit(boolean isAutoTimeout) {
        AppState state = navigationController.getAppState();
        QuizService quizService = state.getQuizService();

        if (quizService.isSubmitted()) {
            return; // Guard against accidental duplicate submissions
        }

        if (!isAutoTimeout) {
            int total = quizService.getTotalQuestions();
            int answered = quizService.getAnsweredCount();
            int unanswered = quizService.getUnansweredCount();

            String message = "Are you sure you want to finalize and submit your quiz?\n\n"
                    + "• Answered Questions: " + answered + " of " + total + "\n"
                    + "• Unanswered Questions: " + unanswered + "\n\n"
                    + "Once submitted, your answers cannot be changed.";

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "Confirm Quiz Submission",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Perform atomic submission and score calculation
        quizService.submitQuiz(state);

        // Transition to Result Screen
        navigationController.navigateTo(NavigationController.SCREEN_RESULT);
    }

    private void handleExit() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit the quiz? Any unsaved answers and progress will be canceled.",
                "Exit Quiz Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            AppState state = navigationController.getAppState();
            state.getQuizService().cancelQuiz();
            navigationController.navigateToHome();
        }
    }

    @Override
    public void onScreenShow(AppState state) {
        QuizService quizService = state.getQuizService();

        String name = quizService.getActivePlayerName();
        if (name == null || name.trim().isEmpty()) {
            name = state.getPlayerName();
        }
        playerIndicator.setText("• Player: " + name);

        Category cat = quizService.getActiveCategory();
        categoryBadge.setText("Category: " + (cat != null ? cat.getDisplayName() : "General"));

        Difficulty diff = quizService.getActiveDifficulty();
        if (diff != null) {
            difficultyBadge.setText("Difficulty: " + diff.getDisplayName());
            difficultyBadge.setForeground(diff.getBadgeColor());
        }

        // Attach Timer listeners
        QuizTimerService timer = quizService.getTimerService();
        timer.clearListeners();

        timer.addTickListener((remaining, formattedTime, isWarning) -> {
            SwingUtilities.invokeLater(() -> {
                this.timerWarningActive = isWarning;
                this.currentTimerText = formattedTime;
                if (isWarning) {
                    timerBadge.setText("⚠ " + formattedTime + " (Hurry!)");
                    timerBadge.setForeground(UIConstants.ERROR);
                } else {
                    timerBadge.setText("⏱ " + formattedTime + " Remaining");
                    timerBadge.setForeground(UIConstants.TEXT_MAIN);
                }
                timerBadge.repaint();
            });
        });

        timer.addTimeoutListener(() -> {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        QuizPanel.this,
                        "Time's up! The quiz timer has expired.\nYour responses are being submitted automatically.",
                        "Time Expired",
                        JOptionPane.INFORMATION_MESSAGE
                );
                handleSubmit(true);
            });
        });

        // Initialize display with current question
        renderCurrentQuestion();
    }
}

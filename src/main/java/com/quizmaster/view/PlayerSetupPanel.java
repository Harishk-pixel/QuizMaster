package com.quizmaster.view;

import com.quizmaster.controller.NavigationController;
import com.quizmaster.model.AppState;
import com.quizmaster.model.Category;
import com.quizmaster.model.Difficulty;
import com.quizmaster.service.QuestionBankService;
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
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Player setup screen allowing player name input, category selection,
 * difficulty level, and configurable question counts (5, 10, 15).
 */
public class PlayerSetupPanel extends JPanel implements NavigationController.RefreshableScreen {

    private final NavigationController navigationController;

    private JTextField nameField;
    private JLabel errorLabel;

    private Category selectedCategory = Category.PROGRAMMING;
    private Difficulty selectedDifficulty = Difficulty.MEDIUM;
    private int selectedQuestionCount = 5;

    private final Map<Category, RoundedPanel> categoryCards = new HashMap<>();
    private final Map<Difficulty, RoundedPanel> difficultyCards = new HashMap<>();
    private final Map<Integer, RoundedPanel> questionCountCards = new HashMap<>();

    public PlayerSetupPanel(NavigationController navigationController) {
        this.navigationController = navigationController;
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_DARK);

        initUI();
    }

    private void initUI() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(UIConstants.BG_DARK);
        container.setBorder(BorderFactory.createEmptyBorder(24, 48, 28, 48));

        // Top Navigation & Header
        container.add(createHeader());
        container.add(Box.createVerticalStrut(20));

        // Player Name Input Card
        container.add(createNameSection());
        container.add(Box.createVerticalStrut(20));

        // Category Selection
        container.add(createCategorySection());
        container.add(Box.createVerticalStrut(20));

        // Difficulty Selection
        container.add(createDifficultySection());
        container.add(Box.createVerticalStrut(20));

        // Question Count Selection
        container.add(createQuestionCountSection());
        container.add(Box.createVerticalStrut(28));

        // Bottom Actions
        container.add(createActionButtons());

        JScrollPane scrollPane = new JScrollPane(container);
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

        JLabel title = new JLabel("Let's Get Started");
        title.setFont(UIConstants.FONT_H1);
        title.setForeground(UIConstants.TEXT_MAIN);

        JLabel subtitle = new JLabel("Enter your name and customize your quiz parameters.");
        subtitle.setFont(UIConstants.FONT_SUBTITLE);
        subtitle.setForeground(UIConstants.TEXT_MUTED);

        textGroup.add(title);
        textGroup.add(subtitle);

        RoundedButton backBtn = new RoundedButton("← Back to Home", RoundedButton.ButtonVariant.SUBTLE);
        backBtn.setPreferredSize(new Dimension(150, 36));
        backBtn.addActionListener(e -> navigationController.navigateToHome());

        header.add(textGroup, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);

        return header;
    }

    private JPanel createNameSection() {
        RoundedPanel card = new RoundedPanel(UIConstants.RADIUS_MD, UIConstants.SURFACE_CARD, UIConstants.BORDER_SUBTLE, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        card.setMaximumSize(new Dimension(1920, 120));

        JLabel label = new JLabel("PLAYER NAME *");
        label.setFont(UIConstants.FONT_BADGE);
        label.setForeground(UIConstants.ACCENT_SECONDARY);

        nameField = Theme.createStyledTextField("Enter your name or gamer tag (e.g. Alex Hunter)");
        nameField.setMaximumSize(new Dimension(1920, 44));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIConstants.FONT_CAPTION);
        errorLabel.setForeground(UIConstants.ERROR);

        card.add(label);
        card.add(Box.createVerticalStrut(8));
        card.add(nameField);
        card.add(Box.createVerticalStrut(4));
        card.add(errorLabel);

        return card;
    }

    private JPanel createCategorySection() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(1920, 180));

        JLabel title = new JLabel("CHOOSE KNOWLEDGE CATEGORY");
        title.setFont(UIConstants.FONT_BADGE);
        title.setForeground(UIConstants.ACCENT_SECONDARY);

        JPanel grid = new JPanel(new GridLayout(1, 5, 12, 0));
        grid.setOpaque(false);

        for (Category cat : Category.values()) {
            RoundedPanel catCard = createCategoryCard(cat);
            categoryCards.put(cat, catCard);
            grid.add(catCard);
        }

        wrapper.add(title);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(grid);

        return wrapper;
    }

    private RoundedPanel createCategoryCard(Category cat) {
        RoundedPanel card = new RoundedPanel(UIConstants.RADIUS_MD, UIConstants.SURFACE_CARD, UIConstants.BORDER_SUBTLE, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel(cat.getSymbol());
        icon.setFont(UIConstants.getFont(Font.PLAIN, 24f));

        JLabel name = new JLabel(cat.getDisplayName());
        name.setFont(UIConstants.FONT_BODY_BOLD);
        name.setForeground(UIConstants.TEXT_MAIN);

        JLabel desc = new JLabel("<html>" + cat.getDescription() + "</html>");
        desc.setFont(UIConstants.FONT_CAPTION);
        desc.setForeground(UIConstants.TEXT_MUTED);

        card.add(icon);
        card.add(Box.createVerticalStrut(6));
        card.add(name);
        card.add(Box.createVerticalStrut(4));
        card.add(desc);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectCategory(cat);
            }
        });

        return card;
    }

    private void selectCategory(Category cat) {
        this.selectedCategory = cat;
        for (Map.Entry<Category, RoundedPanel> entry : categoryCards.entrySet()) {
            boolean active = entry.getKey() == cat;
            RoundedPanel p = entry.getValue();
            p.setBackgroundColor(active ? UIConstants.SURFACE_ELEVATED : UIConstants.SURFACE_CARD);
            p.setBorderColor(active ? UIConstants.ACCENT_PRIMARY : UIConstants.BORDER_SUBTLE);
            p.repaint();
        }
    }

    private JPanel createDifficultySection() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(1920, 130));

        JLabel title = new JLabel("SELECT DIFFICULTY LEVEL");
        title.setFont(UIConstants.FONT_BADGE);
        title.setForeground(UIConstants.ACCENT_SECONDARY);

        JPanel grid = new JPanel(new GridLayout(1, 3, 16, 0));
        grid.setOpaque(false);

        for (Difficulty diff : Difficulty.values()) {
            RoundedPanel diffCard = createDifficultyCard(diff);
            difficultyCards.put(diff, diffCard);
            grid.add(diffCard);
        }

        wrapper.add(title);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(grid);

        return wrapper;
    }

    private RoundedPanel createDifficultyCard(Difficulty diff) {
        RoundedPanel card = new RoundedPanel(UIConstants.RADIUS_MD, UIConstants.SURFACE_CARD, UIConstants.BORDER_SUBTLE, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);

        JLabel badge = Theme.createBadge(diff.getDisplayName().toUpperCase(), new Color(diff.getBadgeColor().getRed(), diff.getBadgeColor().getGreen(), diff.getBadgeColor().getBlue(), 40), diff.getBadgeColor());
        topRow.add(badge);

        JLabel desc = new JLabel(diff.getDescription());
        desc.setFont(UIConstants.FONT_CAPTION);
        desc.setForeground(UIConstants.TEXT_MUTED);

        card.add(topRow);
        card.add(Box.createVerticalStrut(6));
        card.add(desc);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectDifficulty(diff);
            }
        });

        return card;
    }

    private void selectDifficulty(Difficulty diff) {
        this.selectedDifficulty = diff;
        for (Map.Entry<Difficulty, RoundedPanel> entry : difficultyCards.entrySet()) {
            boolean active = entry.getKey() == diff;
            RoundedPanel p = entry.getValue();
            p.setBackgroundColor(active ? UIConstants.SURFACE_ELEVATED : UIConstants.SURFACE_CARD);
            p.setBorderColor(active ? diff.getBadgeColor() : UIConstants.BORDER_SUBTLE);
            p.repaint();
        }
    }

    private JPanel createQuestionCountSection() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(1920, 110));

        JLabel title = new JLabel("NUMBER OF QUESTIONS (30s per question)");
        title.setFont(UIConstants.FONT_BADGE);
        title.setForeground(UIConstants.ACCENT_SECONDARY);

        JPanel grid = new JPanel(new GridLayout(1, 3, 16, 0));
        grid.setOpaque(false);

        int[] counts = {5, 10, 15};
        String[] descs = {"Quick Warm-up (2m 30s timer)", "Standard Quiz (5m 00s timer)", "Comprehensive Challenge (7m 30s timer)"};

        for (int i = 0; i < counts.length; i++) {
            final int count = counts[i];
            String desc = descs[i];

            RoundedPanel card = new RoundedPanel(UIConstants.RADIUS_MD, UIConstants.SURFACE_CARD, UIConstants.BORDER_SUBTLE, 1);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel countLabel = new JLabel(count + " Questions");
            countLabel.setFont(UIConstants.FONT_BODY_BOLD);
            countLabel.setForeground(UIConstants.TEXT_MAIN);

            JLabel descLabel = new JLabel(desc);
            descLabel.setFont(UIConstants.FONT_CAPTION);
            descLabel.setForeground(UIConstants.TEXT_MUTED);

            card.add(countLabel);
            card.add(Box.createVerticalStrut(4));
            card.add(descLabel);

            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectQuestionCount(count);
                }
            });

            questionCountCards.put(count, card);
            grid.add(card);
        }

        wrapper.add(title);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(grid);

        return wrapper;
    }

    private void selectQuestionCount(int count) {
        this.selectedQuestionCount = count;
        for (Map.Entry<Integer, RoundedPanel> entry : questionCountCards.entrySet()) {
            boolean active = entry.getKey() == count;
            RoundedPanel p = entry.getValue();
            p.setBackgroundColor(active ? UIConstants.SURFACE_ELEVATED : UIConstants.SURFACE_CARD);
            p.setBorderColor(active ? UIConstants.ACCENT_PRIMARY : UIConstants.BORDER_SUBTLE);
            p.repaint();
        }
    }

    private JPanel createActionButtons() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        actions.setOpaque(false);
        actions.setMaximumSize(new Dimension(1920, 50));

        RoundedButton cancelBtn = new RoundedButton("Back to Home", RoundedButton.ButtonVariant.OUTLINE);
        cancelBtn.setPreferredSize(new Dimension(150, 44));
        cancelBtn.addActionListener(e -> navigationController.navigateToHome());

        RoundedButton continueBtn = new RoundedButton("Start Quiz Session →", RoundedButton.ButtonVariant.PRIMARY);
        continueBtn.setPreferredSize(new Dimension(200, 44));
        continueBtn.addActionListener(e -> handleContinue());

        actions.add(cancelBtn);
        actions.add(continueBtn);

        return actions;
    }

    private void handleContinue() {
        String enteredName = nameField.getText().trim();
        if (enteredName.isEmpty()) {
            errorLabel.setText("⚠ Please enter your player name to start the quiz.");
            nameField.requestFocusInWindow();
            return;
        }

        if (enteredName.length() < 2) {
            errorLabel.setText("⚠ Player name must be at least 2 characters long.");
            nameField.requestFocusInWindow();
            return;
        }

        AppState state = navigationController.getAppState();
        QuestionBankService bank = state.getQuestionBankService();

        // Validate question count availability
        int available = bank.getAvailableQuestionCount(selectedCategory, selectedDifficulty);
        if (available < selectedQuestionCount) {
            errorLabel.setText("⚠ Only " + available + " questions available for "
                    + selectedCategory.getDisplayName() + " (" + selectedDifficulty.getDisplayName()
                    + "). Please select " + (available >= 5 ? "5 questions" : "another category/difficulty") + ".");
            return;
        }

        errorLabel.setText(" "); // Clear error

        // Store configuration into AppState
        state.setPlayerName(enteredName);
        state.setSelectedCategory(selectedCategory);
        state.setSelectedDifficulty(selectedDifficulty);
        state.setSelectedQuestionCount(selectedQuestionCount);

        // Start active quiz session in QuizService
        try {
            state.getQuizService().startQuiz(enteredName, selectedCategory, selectedDifficulty, selectedQuestionCount);
            navigationController.navigateTo(NavigationController.SCREEN_QUIZ);
        } catch (Exception ex) {
            errorLabel.setText("⚠ " + ex.getMessage());
        }
    }

    @Override
    public void onScreenShow(AppState state) {
        // Restore player name and selected values if already present
        if (state.getPlayerName() != null && !state.getPlayerName().isEmpty()) {
            nameField.setText(state.getPlayerName());
        }
        if (state.getSelectedCategory() != null) {
            selectCategory(state.getSelectedCategory());
        } else {
            selectCategory(Category.PROGRAMMING);
        }
        if (state.getSelectedDifficulty() != null) {
            selectDifficulty(state.getSelectedDifficulty());
        } else {
            selectDifficulty(Difficulty.MEDIUM);
        }
        selectQuestionCount(state.getSelectedQuestionCount() > 0 ? state.getSelectedQuestionCount() : 5);
        errorLabel.setText(" ");
    }
}

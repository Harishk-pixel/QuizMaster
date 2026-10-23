package com.quizmaster.view;

import com.quizmaster.controller.NavigationController;
import com.quizmaster.database.DatabaseConnection;
import com.quizmaster.model.AppState;
import com.quizmaster.model.Category;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.QuizAttempt;
import com.quizmaster.util.CustomScrollBarUI;
import com.quizmaster.util.RoundedButton;
import com.quizmaster.util.RoundedPanel;
import com.quizmaster.util.Theme;
import com.quizmaster.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Score History screen displaying summary performance cards, category & player filters,
 * live database synchronization, and a custom-styled dark JTable with attempt records.
 */
public class ScoreHistoryPanel extends JPanel implements NavigationController.RefreshableScreen {

    private final NavigationController navigationController;

    private JLabel highestScoreLabel;
    private JLabel averageScoreLabel;
    private JLabel totalAttemptsLabel;
    private JLabel dbStatusBadge;

    private JComboBox<String> categoryFilter;
    private JTextField searchField;

    private JTable table;
    private DefaultTableModel tableModel;
    private JPanel emptyStatePanel;
    private JScrollPane tableScrollPane;

    private List<QuizAttempt> allAttempts = new ArrayList<>();
    private final List<QuizAttempt> displayedAttempts = new ArrayList<>();

    public ScoreHistoryPanel(NavigationController navigationController) {
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

        // Header Bar with DB connection status
        content.add(createHeader());
        content.add(Box.createVerticalStrut(24));

        // 3 Summary Performance Metric Cards
        content.add(createSummaryCardsRow());
        content.add(Box.createVerticalStrut(24));

        // Filter / Search / Refresh Toolbar
        content.add(createFilterToolbar());
        content.add(Box.createVerticalStrut(16));

        // Table / Empty State Container
        content.add(createTableSection());
        content.add(Box.createVerticalStrut(24));

        // Bottom Actions (Review Selected Attempt, New Quiz, Home)
        content.add(createBottomBar());

        JScrollPane mainScroll = new JScrollPane(content);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        mainScroll.getViewport().setBackground(UIConstants.BG_DARK);

        add(mainScroll, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(1920, 50));

        JPanel textGroup = new JPanel();
        textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));
        textGroup.setOpaque(false);

        JLabel title = new JLabel("Your Performance & Score History");
        title.setFont(UIConstants.FONT_H1);
        title.setForeground(UIConstants.TEXT_MAIN);

        JLabel subtitle = new JLabel("Review persistent quiz attempts, accuracy trends, and detailed answers.");
        subtitle.setFont(UIConstants.FONT_SUBTITLE);
        subtitle.setForeground(UIConstants.TEXT_MUTED);

        textGroup.add(title);
        textGroup.add(subtitle);

        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightGroup.setOpaque(false);

        dbStatusBadge = Theme.createBadge("● Database: Checking...", UIConstants.SURFACE_SECONDARY, UIConstants.TEXT_MUTED);

        RoundedButton homeBtn = new RoundedButton("← Back to Home", RoundedButton.ButtonVariant.SUBTLE);
        homeBtn.setPreferredSize(new Dimension(140, 36));
        homeBtn.addActionListener(e -> navigationController.navigateToHome());

        rightGroup.add(dbStatusBadge);
        rightGroup.add(homeBtn);

        header.add(textGroup, BorderLayout.WEST);
        header.add(rightGroup, BorderLayout.EAST);

        return header;
    }

    private JPanel createSummaryCardsRow() {
        JPanel grid = new JPanel(new GridLayout(1, 3, 20, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(1920, 110));

        // 1. Highest Score
        highestScoreLabel = new JLabel("0 / 0");
        grid.add(createStatCard("HIGHEST SCORE", highestScoreLabel, "Personal Best Result", UIConstants.SUCCESS, "🏆"));

        // 2. Average Score
        averageScoreLabel = new JLabel("0%");
        grid.add(createStatCard("AVERAGE SCORE", averageScoreLabel, "Overall Accuracy", UIConstants.ACCENT_PRIMARY, "📈"));

        // 3. Total Attempts
        totalAttemptsLabel = new JLabel("0 Completed");
        grid.add(createStatCard("TOTAL ATTEMPTS", totalAttemptsLabel, "Recorded Quiz Sessions", UIConstants.ACCENT_SECONDARY, "🎯"));

        return grid;
    }

    private RoundedPanel createStatCard(String title, JLabel valLabel, String sub, Color accent, String icon) {
        RoundedPanel card = new RoundedPanel(UIConstants.RADIUS_MD, UIConstants.SURFACE_CARD, UIConstants.BORDER_SUBTLE, 1);
        card.setLayout(new BorderLayout(16, 0));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel tLabel = new JLabel(title);
        tLabel.setFont(UIConstants.FONT_BADGE);
        tLabel.setForeground(accent);

        valLabel.setFont(UIConstants.getFont(Font.BOLD, 22f));
        valLabel.setForeground(UIConstants.TEXT_MAIN);

        JLabel sLabel = new JLabel(sub);
        sLabel.setFont(UIConstants.FONT_CAPTION);
        sLabel.setForeground(UIConstants.TEXT_MUTED);

        textPanel.add(tLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(valLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(sLabel);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(UIConstants.getFont(Font.PLAIN, 28f));

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconLabel, BorderLayout.EAST);

        return card;
    }

    private JPanel createFilterToolbar() {
        RoundedPanel toolbar = new RoundedPanel(UIConstants.RADIUS_MD, UIConstants.SURFACE_CARD, UIConstants.BORDER_SUBTLE, 1);
        toolbar.setLayout(new BorderLayout(16, 0));
        toolbar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        toolbar.setMaximumSize(new Dimension(1920, 60));

        // Left: Filter by Category dropdown
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel filterLabel = new JLabel("Filter Category:");
        filterLabel.setFont(UIConstants.FONT_CAPTION);
        filterLabel.setForeground(UIConstants.TEXT_MUTED);

        String[] categories = new String[Category.values().length + 1];
        categories[0] = "All Categories";
        for (int i = 0; i < Category.values().length; i++) {
            categories[i + 1] = Category.values()[i].getDisplayName();
        }
        categoryFilter = Theme.createStyledComboBox(categories);
        categoryFilter.setPreferredSize(new Dimension(190, 36));
        categoryFilter.addActionListener(e -> applyFilter());

        left.add(filterLabel);
        left.add(categoryFilter);

        // Right: Search Input Field & Refresh Button
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        searchField = Theme.createStyledTextField("Search player, attempt ID...");
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilter();
            }
        });

        RoundedButton resetBtn = new RoundedButton("Reset", RoundedButton.ButtonVariant.SUBTLE);
        resetBtn.setPreferredSize(new Dimension(75, 36));
        resetBtn.setFont(UIConstants.FONT_CAPTION);
        resetBtn.addActionListener(e -> {
            categoryFilter.setSelectedIndex(0);
            searchField.setText("");
            applyFilter();
        });

        RoundedButton refreshBtn = new RoundedButton("🔄 Refresh", RoundedButton.ButtonVariant.OUTLINE);
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.setFont(UIConstants.FONT_CAPTION);
        refreshBtn.addActionListener(e -> handleRefresh());

        right.add(searchField);
        right.add(resetBtn);
        right.add(refreshBtn);

        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);

        return toolbar;
    }

    private JPanel createTableSection() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        String[] columnNames = {"Attempt ID", "Player", "Date & Time", "Category", "Difficulty", "Score", "Percentage", "Duration"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(44);
        table.setShowGrid(true);
        table.setGridColor(UIConstants.BORDER_SUBTLE);
        table.setBackground(UIConstants.SURFACE_CARD);
        table.setForeground(UIConstants.TEXT_MAIN);
        table.setSelectionBackground(UIConstants.ACCENT_PRIMARY_LIGHT);
        table.setSelectionForeground(UIConstants.TEXT_MAIN);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));

        // Custom cell renderer for styled cells
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
                setFont(UIConstants.FONT_BODY);

                if (!isSelected) {
                    setBackground(row % 2 == 0 ? UIConstants.SURFACE_CARD : UIConstants.SURFACE_SECONDARY);
                    setForeground(UIConstants.TEXT_MAIN);
                }

                if (column == 0) { // Attempt ID
                    setForeground(UIConstants.ACCENT_SECONDARY);
                    setFont(UIConstants.FONT_BODY_BOLD);
                } else if (column == 1) { // Player Name
                    setForeground(UIConstants.TEXT_MAIN);
                    setFont(UIConstants.FONT_BODY_BOLD);
                } else if (column == 4) { // Difficulty
                    String diff = String.valueOf(value);
                    if ("Easy".equalsIgnoreCase(diff)) setForeground(UIConstants.SUCCESS);
                    else if ("Medium".equalsIgnoreCase(diff)) setForeground(UIConstants.WARNING);
                    else if ("Hard".equalsIgnoreCase(diff)) setForeground(UIConstants.ERROR);
                } else if (column == 6) { // Percentage
                    setForeground(UIConstants.SUCCESS);
                    setFont(UIConstants.FONT_BODY_BOLD);
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                return c;
            }
        });

        tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_SUBTLE, 1));
        tableScrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        tableScrollPane.getViewport().setBackground(UIConstants.SURFACE_CARD);
        tableScrollPane.setPreferredSize(new Dimension(1920, 260));

        // Empty state panel
        emptyStatePanel = createEmptyStatePanel();
        emptyStatePanel.setVisible(false);

        wrapper.add(tableScrollPane, BorderLayout.CENTER);
        wrapper.add(emptyStatePanel, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createEmptyStatePanel() {
        RoundedPanel panel = new RoundedPanel(UIConstants.RADIUS_MD, UIConstants.SURFACE_CARD, UIConstants.BORDER_SUBTLE, 1);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        JLabel icon = new JLabel("🔍");
        icon.setFont(UIConstants.getFont(Font.PLAIN, 36f));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("No Quiz Attempts Found");
        title.setFont(UIConstants.FONT_H3);
        title.setForeground(UIConstants.TEXT_MAIN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel desc = new JLabel("No attempt records matched your filter or search query.");
        desc.setFont(UIConstants.FONT_CAPTION);
        desc.setForeground(UIConstants.TEXT_MUTED);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(icon);
        panel.add(Box.createVerticalStrut(10));
        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(desc);

        return panel;
    }

    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setMaximumSize(new Dimension(1920, 50));

        JLabel note = new JLabel("Select any attempt row and click 'Review Attempt' to inspect answer details.");
        note.setFont(UIConstants.FONT_CAPTION);
        note.setForeground(UIConstants.TEXT_DIM);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);

        RoundedButton reviewSelectedBtn = new RoundedButton("Review Attempt 🔍", RoundedButton.ButtonVariant.SECONDARY);
        reviewSelectedBtn.setPreferredSize(new Dimension(160, 42));
        reviewSelectedBtn.addActionListener(e -> handleReviewSelected());

        RoundedButton homeBtn = new RoundedButton("Back to Home", RoundedButton.ButtonVariant.OUTLINE);
        homeBtn.setPreferredSize(new Dimension(140, 42));
        homeBtn.addActionListener(e -> navigationController.navigateToHome());

        RoundedButton newQuizBtn = new RoundedButton("Start New Quiz", RoundedButton.ButtonVariant.PRIMARY);
        newQuizBtn.setPreferredSize(new Dimension(150, 42));
        newQuizBtn.addActionListener(e -> navigationController.navigateTo(NavigationController.SCREEN_PLAYER_SETUP));

        actions.add(reviewSelectedBtn);
        actions.add(homeBtn);
        actions.add(newQuizBtn);

        bar.add(note, BorderLayout.WEST);
        bar.add(actions, BorderLayout.EAST);

        return bar;
    }

    private void handleReviewSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= displayedAttempts.size()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an attempt row from the table first.",
                    "No Attempt Selected",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        QuizAttempt attempt = displayedAttempts.get(selectedRow);
        AppState state = navigationController.getAppState();
        state.setSelectedHistoricalAttempt(attempt);

        navigationController.navigateTo(NavigationController.SCREEN_ANSWER_REVIEW);
    }

    private void handleRefresh() {
        AppState state = navigationController.getAppState();
        boolean loaded = state.refreshHistoryFromDatabase();

        if (loaded) {
            this.allAttempts = new ArrayList<>(state.getScoreHistory());
            updateSummaryCards();
            applyFilter();
            JOptionPane.showMessageDialog(this, "Score history refreshed from MySQL database.", "History Refreshed", JOptionPane.INFORMATION_MESSAGE);
        } else {
            DatabaseConnection.ConnectionStatus status = state.getPersistenceService().checkDatabaseStatus();
            JOptionPane.showMessageDialog(this,
                    "Unable to refresh from MySQL database.\n" + status.getMessage() + "\nShowing local cached attempts.",
                    "Database Offline",
                    JOptionPane.WARNING_MESSAGE);
        }
        updateDatabaseStatusBadge(state);
    }

    private void updateDatabaseStatusBadge(AppState state) {
        DatabaseConnection.ConnectionStatus status = state.getPersistenceService().checkDatabaseStatus();
        if (status.isConnected()) {
            dbStatusBadge.setText("● MySQL: Connected (" + status.getResponseTimeMs() + "ms)");
            dbStatusBadge.setForeground(UIConstants.SUCCESS);
        } else {
            dbStatusBadge.setText("○ MySQL: Offline (Local Mode)");
            dbStatusBadge.setForeground(UIConstants.WARNING);
        }
        dbStatusBadge.repaint();
    }

    private void updateSummaryCards() {
        if (!allAttempts.isEmpty()) {
            int maxScore = 0;
            int totalPercent = 0;
            for (QuizAttempt att : allAttempts) {
                if (att.getScore() > maxScore) maxScore = att.getScore();
                totalPercent += att.getPercentage();
            }
            int avgPercent = Math.round((float) totalPercent / allAttempts.size());

            highestScoreLabel.setText(maxScore + " pts");
            averageScoreLabel.setText(avgPercent + "%");
            totalAttemptsLabel.setText(allAttempts.size() + " Completed");
        } else {
            highestScoreLabel.setText("0 / 0");
            averageScoreLabel.setText("0%");
            totalAttemptsLabel.setText("0 Completed");
        }
    }

    private void applyFilter() {
        String selectedCat = (String) categoryFilter.getSelectedItem();
        String search = searchField.getText().trim().toLowerCase();

        tableModel.setRowCount(0);
        displayedAttempts.clear();

        for (QuizAttempt att : allAttempts) {
            String catName = att.getCategory() != null ? att.getCategory().getDisplayName() : "General";
            String diffName = att.getDifficulty() != null ? att.getDifficulty().getDisplayName() : "Standard";

            boolean matchesCat = selectedCat == null
                    || "All Categories".equals(selectedCat)
                    || catName.equalsIgnoreCase(selectedCat);

            boolean matchesSearch = search.isEmpty()
                    || (att.getAttemptId() != null && att.getAttemptId().toLowerCase().contains(search))
                    || (att.getPlayerName() != null && att.getPlayerName().toLowerCase().contains(search))
                    || catName.toLowerCase().contains(search)
                    || diffName.toLowerCase().contains(search);

            if (matchesCat && matchesSearch) {
                displayedAttempts.add(att);
                tableModel.addRow(new Object[]{
                        att.getAttemptId(),
                        att.getPlayerName(),
                        att.getDate(),
                        catName,
                        diffName,
                        att.getScoreFormatted(),
                        att.getPercentage() + "%",
                        att.getTimeTakenFormatted()
                });
            }
        }

        if (displayedAttempts.isEmpty()) {
            tableScrollPane.setVisible(false);
            emptyStatePanel.setVisible(true);
        } else {
            tableScrollPane.setVisible(true);
            emptyStatePanel.setVisible(false);
        }

        revalidate();
        repaint();
    }

    @Override
    public void onScreenShow(AppState state) {
        // Try refreshing from database if available
        state.refreshHistoryFromDatabase();
        this.allAttempts = new ArrayList<>(state.getScoreHistory());

        updateDatabaseStatusBadge(state);
        updateSummaryCards();
        applyFilter();
    }
}

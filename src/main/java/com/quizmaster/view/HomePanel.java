package com.quizmaster.view;

import com.quizmaster.controller.NavigationController;
import com.quizmaster.model.AppState;
import com.quizmaster.util.CustomScrollBarUI;
import com.quizmaster.util.RoundedButton;
import com.quizmaster.util.RoundedPanel;
import com.quizmaster.util.UIConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * Home/Landing screen for QuizMaster featuring hero section, feature cards, and navigation actions.
 */
public class HomePanel extends JPanel implements NavigationController.RefreshableScreen {

    private final NavigationController navigationController;

    public HomePanel(NavigationController navigationController) {
        this.navigationController = navigationController;
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_DARK);

        initUI();
    }

    private void initUI() {
        // Main content inside a clean scroll pane for responsive scaling
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIConstants.BG_DARK);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 48, 24, 48));

        // Top Navigation Header
        contentPanel.add(createHeaderBar());
        contentPanel.add(Box.createVerticalStrut(32));

        // Hero Section
        contentPanel.add(createHeroSection());
        contentPanel.add(Box.createVerticalStrut(40));

        // Feature Highlights Section
        contentPanel.add(createFeatureCardsSection());
        contentPanel.add(Box.createVerticalStrut(32));

        // Footer
        contentPanel.add(createFooter());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getViewport().setBackground(UIConstants.BG_DARK);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(1920, 60));

        // Logo + App Name + Subtitle
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        brandPanel.setOpaque(false);

        // Custom drawn Logo Icon
        JPanel logoIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient icon background
                g2.setColor(UIConstants.ACCENT_PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(UIConstants.TEXT_MAIN);
                g2.setFont(UIConstants.getFont(java.awt.Font.BOLD, 20f));
                g2.drawString("Q", 13, 27);
                g2.dispose();
            }
        };
        logoIcon.setPreferredSize(new Dimension(40, 40));
        logoIcon.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);

        JLabel appTitle = new JLabel("QuizMaster");
        appTitle.setFont(UIConstants.FONT_LOGO);
        appTitle.setForeground(UIConstants.TEXT_MAIN);

        JLabel appSubtitle = new JLabel("Challenge Your Knowledge. Track Your Progress.");
        appSubtitle.setFont(UIConstants.FONT_CAPTION);
        appSubtitle.setForeground(UIConstants.TEXT_MUTED);

        titles.add(appTitle);
        titles.add(appSubtitle);

        brandPanel.add(logoIcon);
        brandPanel.add(titles);

        // Header Action: Quick History Button
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);

        RoundedButton historyBtn = new RoundedButton("Score History", RoundedButton.ButtonVariant.OUTLINE);
        historyBtn.setPreferredSize(new Dimension(140, 38));
        historyBtn.addActionListener(e -> navigationController.navigateTo(NavigationController.SCREEN_SCORE_HISTORY));

        RoundedButton startBtn = new RoundedButton("Get Started", RoundedButton.ButtonVariant.PRIMARY);
        startBtn.setPreferredSize(new Dimension(130, 38));
        startBtn.addActionListener(e -> navigationController.navigateTo(NavigationController.SCREEN_PLAYER_SETUP));

        actions.add(historyBtn);
        actions.add(startBtn);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        return header;
    }

    private JPanel createHeroSection() {
        RoundedPanel heroCard = new RoundedPanel(UIConstants.RADIUS_LG, UIConstants.SURFACE_CARD, UIConstants.BORDER_SUBTLE, 1);
        heroCard.setLayout(new GridBagLayout());
        heroCard.setMaximumSize(new Dimension(1920, 320));
        heroCard.setBorder(BorderFactory.createEmptyBorder(40, 48, 40, 48));

        GridBagConstraints gbc = new GridBagConstraints();

        // Left Column: Text and Call to Action
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel tagLabel = new JLabel("DESKTOP QUIZ PLATFORM");
        tagLabel.setFont(UIConstants.FONT_BADGE);
        tagLabel.setForeground(UIConstants.ACCENT_SECONDARY);

        JLabel heroHeading = new JLabel("Ready to Challenge Yourself?");
        heroHeading.setFont(UIConstants.FONT_HERO);
        heroHeading.setForeground(UIConstants.TEXT_MAIN);

        JLabel heroDescription = new JLabel(
                "<html>Test your conceptual mastery across core computer science domains. "
                + "Experience structured assessments, real-time feedback, and analytical performance tracking.</html>"
        );
        heroDescription.setFont(UIConstants.FONT_BODY_MEDIUM);
        heroDescription.setForeground(UIConstants.TEXT_MUTED);
        heroDescription.setPreferredSize(new Dimension(520, 50));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        buttonRow.setOpaque(false);
        buttonRow.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        RoundedButton startQuizBtn = new RoundedButton("Start Quiz", RoundedButton.ButtonVariant.PRIMARY);
        startQuizBtn.setPreferredSize(new Dimension(160, 46));
        startQuizBtn.setFont(UIConstants.FONT_BODY_BOLD);
        startQuizBtn.addActionListener(e -> navigationController.navigateTo(NavigationController.SCREEN_PLAYER_SETUP));

        RoundedButton scoreHistoryBtn = new RoundedButton("View Score History", RoundedButton.ButtonVariant.OUTLINE);
        scoreHistoryBtn.setPreferredSize(new Dimension(180, 46));
        scoreHistoryBtn.setFont(UIConstants.FONT_BODY_BOLD);
        scoreHistoryBtn.addActionListener(e -> navigationController.navigateTo(NavigationController.SCREEN_SCORE_HISTORY));

        buttonRow.add(startQuizBtn);
        buttonRow.add(scoreHistoryBtn);

        textPanel.add(tagLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(heroHeading);
        textPanel.add(Box.createVerticalStrut(12));
        textPanel.add(heroDescription);
        textPanel.add(buttonRow);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.65;
        gbc.fill = GridBagConstraints.BOTH;
        heroCard.add(textPanel, gbc);

        // Right Column: Stylized Visual Card
        JPanel visualPanel = createIllustrationCard();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.EAST;
        heroCard.add(visualPanel, gbc);

        return heroCard;
    }

    private JPanel createIllustrationCard() {
        RoundedPanel badge = new RoundedPanel(UIConstants.RADIUS_MD, UIConstants.SURFACE_SECONDARY, UIConstants.BORDER_SUBTLE, 1);
        badge.setLayout(new BoxLayout(badge, BoxLayout.Y_AXIS));
        badge.setPreferredSize(new Dimension(280, 210));
        badge.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Quiz Mastery");
        title.setFont(UIConstants.FONT_H3);
        title.setForeground(UIConstants.TEXT_MAIN);

        JLabel status = new JLabel("● Live Assessment Engine");
        status.setFont(UIConstants.FONT_CAPTION);
        status.setForeground(UIConstants.SUCCESS);

        JPanel scorePreview = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        scorePreview.setOpaque(false);
        JLabel bigScore = new JLabel("92%");
        bigScore.setFont(UIConstants.getFont(java.awt.Font.BOLD, 36f));
        bigScore.setForeground(UIConstants.ACCENT_PRIMARY);

        JLabel scoreSub = new JLabel("Top Tier");
        scoreSub.setFont(UIConstants.FONT_BADGE);
        scoreSub.setForeground(UIConstants.TEXT_MUTED);

        scorePreview.add(bigScore);
        scorePreview.add(scoreSub);

        JPanel categoryRow = new JPanel(new GridLayout(2, 2, 8, 8));
        categoryRow.setOpaque(false);

        String[] tags = {"General", "Science", "CS", "Math"};
        for (String tag : tags) {
            JLabel pill = new JLabel(tag, SwingConstants.CENTER);
            pill.setFont(UIConstants.FONT_BADGE);
            pill.setForeground(UIConstants.TEXT_MUTED);
            pill.setOpaque(true);
            pill.setBackground(new Color(0x1E, 0x2C, 0x48));
            pill.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            categoryRow.add(pill);
        }

        badge.add(title);
        badge.add(Box.createVerticalStrut(6));
        badge.add(status);
        badge.add(Box.createVerticalStrut(14));
        badge.add(scorePreview);
        badge.add(Box.createVerticalStrut(14));
        badge.add(categoryRow);

        return badge;
    }

    private JPanel createFeatureCardsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setMaximumSize(new Dimension(1920, 240));

        JLabel sectionTitle = new JLabel("Core Platform Features");
        sectionTitle.setFont(UIConstants.FONT_H2);
        sectionTitle.setForeground(UIConstants.TEXT_MAIN);

        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, 24, 0));
        cardsGrid.setOpaque(false);

        // Feature Card 1: Multiple Categories
        cardsGrid.add(createFeatureCard(
                "Multiple Categories",
                "Explore diverse categories: General Knowledge, Science, Computer Science, Mathematics, and Programming.",
                "📚",
                UIConstants.ACCENT_PRIMARY
        ));

        // Feature Card 2: Track Your Progress
        cardsGrid.add(createFeatureCard(
                "Track Your Progress",
                "Review complete score history, accuracy percentages, and detailed answer breakdowns.",
                "📊",
                UIConstants.ACCENT_SECONDARY
        ));

        // Feature Card 3: Challenge Yourself
        cardsGrid.add(createFeatureCard(
                "Challenge Yourself",
                "Test your expertise across Easy, Medium, and Hard difficulty levels tailored to your level.",
                "🎯",
                UIConstants.SUCCESS
        ));

        section.add(sectionTitle);
        section.add(Box.createVerticalStrut(16));
        section.add(cardsGrid);

        return section;
    }

    private JPanel createFeatureCard(String title, String description, String icon, Color accentColor) {
        RoundedPanel card = new RoundedPanel(UIConstants.RADIUS_MD, UIConstants.SURFACE_CARD, UIConstants.BORDER_SUBTLE, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.enableHoverEffect(UIConstants.SURFACE_CARD_HOVER, accentColor);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Icon Pill
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(UIConstants.getFont(java.awt.Font.PLAIN, 28f));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.FONT_H3);
        titleLabel.setForeground(UIConstants.TEXT_MAIN);

        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(UIConstants.FONT_BODY);
        descLabel.setForeground(UIConstants.TEXT_MUTED);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(descLabel);

        return card;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 16));
        footer.setOpaque(false);
        footer.setMaximumSize(new Dimension(1920, 60));

        JLabel footerText = new JLabel("QuizMaster | Java Desktop Application");
        footerText.setFont(UIConstants.FONT_CAPTION);
        footerText.setForeground(UIConstants.TEXT_DIM);

        footer.add(footerText);
        return footer;
    }

    @Override
    public void onScreenShow(AppState state) {
        // Refresh if needed
    }
}

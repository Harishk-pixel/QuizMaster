package com.quizmaster.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxUI;

/**
 * Global application theming utilities and component factories.
 */
public final class Theme {

    private Theme() {}

    /**
     * Initializes global Swing Look and Feel properties to ensure cohesive dark mode rendering.
     */
    public static void applyGlobalTheme() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        UIManager.put("Panel.background", UIConstants.BG_DARK);
        UIManager.put("Viewport.background", UIConstants.BG_DARK);
        UIManager.put("ScrollPane.background", UIConstants.BG_DARK);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());

        UIManager.put("ToolTip.background", UIConstants.SURFACE_CARD);
        UIManager.put("ToolTip.foreground", UIConstants.TEXT_MAIN);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(UIConstants.BORDER_SUBTLE));
        UIManager.put("ToolTip.font", UIConstants.FONT_CAPTION);

        UIManager.put("Label.foreground", UIConstants.TEXT_MAIN);
        UIManager.put("Label.font", UIConstants.FONT_BODY);

        UIManager.put("TableHeader.background", UIConstants.SURFACE_SECONDARY);
        UIManager.put("TableHeader.foreground", UIConstants.TEXT_MUTED);
        UIManager.put("TableHeader.font", UIConstants.FONT_BODY_BOLD);

        UIManager.put("Table.background", UIConstants.SURFACE_CARD);
        UIManager.put("Table.foreground", UIConstants.TEXT_MAIN);
        UIManager.put("Table.gridColor", UIConstants.BORDER_SUBTLE);
        UIManager.put("Table.selectionBackground", UIConstants.ACCENT_PRIMARY_LIGHT);
        UIManager.put("Table.selectionForeground", UIConstants.TEXT_MAIN);
        UIManager.put("Table.font", UIConstants.FONT_BODY);
        UIManager.put("Table.rowHeight", 44);
    }

    /**
     * Creates a custom modern text input field with placeholder and focus border animation.
     */
    public static JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField() {
            private boolean focused = false;

            {
                addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        focused = true;
                        repaint();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        focused = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Background
                g2.setColor(UIConstants.SURFACE_SECONDARY);
                g2.fillRoundRect(0, 0, w, h, UIConstants.RADIUS_MD, UIConstants.RADIUS_MD);

                // Border
                g2.setColor(focused ? UIConstants.BORDER_ACTIVE : UIConstants.BORDER_SUBTLE);
                g2.setStroke(new BasicStroke(focused ? 1.8f : 1.0f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, UIConstants.RADIUS_MD, UIConstants.RADIUS_MD);

                g2.dispose();
                super.paintComponent(g);

                // Placeholder
                if (getText().isEmpty() && !focused && placeholder != null) {
                    Graphics2D gp = (Graphics2D) g.create();
                    gp.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    gp.setColor(UIConstants.TEXT_DIM);
                    gp.setFont(UIConstants.FONT_BODY);
                    Insets insets = getInsets();
                    int y = (h - gp.getFontMetrics().getHeight()) / 2 + gp.getFontMetrics().getAscent();
                    gp.drawString(placeholder, insets.left, y);
                    gp.dispose();
                }
            }
        };

        textField.setOpaque(false);
        textField.setBackground(new Color(0, 0, 0, 0));
        textField.setForeground(UIConstants.TEXT_MAIN);
        textField.setCaretColor(UIConstants.ACCENT_PRIMARY);
        textField.setFont(UIConstants.FONT_BODY);
        textField.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        textField.setPreferredSize(new Dimension(320, 44));
        return textField;
    }

    /**
     * Creates a styled badge label with rounded background.
     */
    public static JLabel createBadge(String text, Color bgColor, Color textColor) {
        JLabel badge = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.RADIUS_PILL, UIConstants.RADIUS_PILL);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(UIConstants.FONT_BADGE);
        badge.setForeground(textColor);
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        return badge;
    }

    /**
     * Creates a styled modern combo box.
     */
    public static <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(UIConstants.FONT_BODY);
        comboBox.setForeground(UIConstants.TEXT_MAIN);
        comboBox.setBackground(UIConstants.SURFACE_SECONDARY);
        comboBox.setFocusable(false);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_SUBTLE, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        comboBox.setUI(new BasicComboBoxUI());
        return comboBox;
    }
}

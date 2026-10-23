package com.quizmaster.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * Modern styled button supporting primary, secondary, outline, danger, and subtle variants.
 */
public class RoundedButton extends JButton {

    public enum ButtonVariant {
        PRIMARY,
        SECONDARY,
        OUTLINE,
        DANGER,
        SUBTLE
    }

    private ButtonVariant variant;
    private int cornerRadius;
    private boolean hovered = false;
    private boolean pressed = false;

    public RoundedButton(String text) {
        this(text, ButtonVariant.PRIMARY);
    }

    public RoundedButton(String text, ButtonVariant variant) {
        super(text);
        this.variant = variant;
        this.cornerRadius = UIConstants.RADIUS_MD;

        setFont(UIConstants.FONT_BUTTON);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 20, 10, 20));

        setupMouseListeners();
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    hovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    pressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    public void setVariant(ButtonVariant variant) {
        this.variant = variant;
        repaint();
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        Color bgColor;
        Color textColor;
        Color borderColor = null;

        if (!isEnabled()) {
            bgColor = new Color(0x18, 0x22, 0x35);
            textColor = UIConstants.TEXT_DIM;
            borderColor = new Color(0x20, 0x2C, 0x44);
        } else {
            switch (variant) {
                case PRIMARY:
                    if (pressed) {
                        bgColor = new Color(0x5A, 0x3B, 0xD4);
                    } else if (hovered) {
                        bgColor = UIConstants.ACCENT_PRIMARY_HOVER;
                    } else {
                        bgColor = UIConstants.ACCENT_PRIMARY;
                    }
                    textColor = UIConstants.TEXT_MAIN;
                    break;

                case SECONDARY:
                    if (pressed) {
                        bgColor = new Color(0x1C, 0x8C, 0xCE);
                    } else if (hovered) {
                        bgColor = UIConstants.ACCENT_SECONDARY_HOVER;
                    } else {
                        bgColor = UIConstants.ACCENT_SECONDARY;
                    }
                    textColor = UIConstants.BG_DARK;
                    break;

                case OUTLINE:
                    if (pressed) {
                        bgColor = new Color(0x24, 0x33, 0x52);
                    } else if (hovered) {
                        bgColor = new Color(0x1C, 0x28, 0x40);
                    } else {
                        bgColor = new Color(0, 0, 0, 0); // Transparent
                    }
                    borderColor = (hovered || isFocusOwner()) ? UIConstants.ACCENT_PRIMARY : UIConstants.BORDER_SUBTLE;
                    textColor = hovered ? UIConstants.TEXT_MAIN : UIConstants.TEXT_MUTED;
                    break;

                case DANGER:
                    if (pressed) {
                        bgColor = new Color(0xD8, 0x47, 0x5D);
                    } else if (hovered) {
                        bgColor = new Color(0xF2, 0x55, 0x6E);
                    } else {
                        bgColor = UIConstants.ERROR;
                    }
                    textColor = UIConstants.TEXT_MAIN;
                    break;

                case SUBTLE:
                default:
                    if (pressed) {
                        bgColor = new Color(0x22, 0x30, 0x4C);
                    } else if (hovered) {
                        bgColor = UIConstants.SURFACE_CARD_HOVER;
                    } else {
                        bgColor = UIConstants.SURFACE_SECONDARY;
                    }
                    borderColor = UIConstants.BORDER_SUBTLE;
                    textColor = hovered ? UIConstants.TEXT_MAIN : UIConstants.TEXT_MUTED;
                    break;
            }
        }

        // Fill background
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Draw border if present
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, width - 2, height - 2, cornerRadius, cornerRadius);
        }

        g2.dispose();

        // Let Swing paint the text and icon cleanly
        setForeground(textColor);
        super.paintComponent(g);
    }
}

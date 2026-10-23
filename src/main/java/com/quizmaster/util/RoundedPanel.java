package com.quizmaster.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * Reusable JPanel with anti-aliased rounded corners, custom borders, and optional hover highlights.
 */
public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private Color backgroundColor;
    private Color hoverBackgroundColor;
    private Color borderColor;
    private Color activeBorderColor;
    private int borderThickness;
    private boolean hovered = false;
    private boolean selected = false;

    public RoundedPanel() {
        this(UIConstants.RADIUS_MD, UIConstants.SURFACE_CARD);
    }

    public RoundedPanel(int cornerRadius, Color backgroundColor) {
        this(cornerRadius, backgroundColor, UIConstants.BORDER_SUBTLE, 1);
    }

    public RoundedPanel(int cornerRadius, Color backgroundColor, Color borderColor, int borderThickness) {
        this.cornerRadius = cornerRadius;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        this.borderThickness = borderThickness;
        setOpaque(false);
    }

    public RoundedPanel(LayoutManager layout) {
        super(layout);
        this.cornerRadius = UIConstants.RADIUS_MD;
        this.backgroundColor = UIConstants.SURFACE_CARD;
        this.borderColor = UIConstants.BORDER_SUBTLE;
        this.borderThickness = 1;
        setOpaque(false);
    }

    public void enableHoverEffect(Color hoverColor, Color hoverBorder) {
        this.hoverBackgroundColor = hoverColor;
        this.activeBorderColor = hoverBorder;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
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

        int width = getWidth();
        int height = getHeight();

        // Determine background
        Color fill = backgroundColor;
        if (selected && hoverBackgroundColor != null) {
            fill = hoverBackgroundColor;
        } else if (hovered && hoverBackgroundColor != null) {
            fill = hoverBackgroundColor;
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Determine border
        Color stroke = borderColor;
        if (selected && activeBorderColor != null) {
            stroke = activeBorderColor;
        } else if (hovered && activeBorderColor != null) {
            stroke = activeBorderColor;
        }

        if (stroke != null && borderThickness > 0) {
            g2.setColor(stroke);
            g2.setStroke(new BasicStroke(borderThickness));
            int offset = borderThickness / 2;
            g2.drawRoundRect(offset, offset, width - borderThickness, height - borderThickness, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}

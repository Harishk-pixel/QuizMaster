package com.quizmaster.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/**
 * Custom-painted circular progress ring showing percentage and score status.
 */
public class CircularProgressIndicator extends JComponent {
    private int progress = 0; // 0 - 100
    private String subtitle = "Accuracy";
    private Color trackColor = new Color(0x1C, 0x2A, 0x44);
    private Color progressColor = UIConstants.ACCENT_PRIMARY;
    private int strokeWidth = 14;

    public CircularProgressIndicator(int progress) {
        this.progress = Math.max(0, Math.min(100, progress));
        setPreferredSize(new Dimension(190, 190));
        setOpaque(false);
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(100, progress));
        // Dynamically tint if desired (e.g. green for >= 80%, warning for >= 60%, error otherwise)
        if (progress >= 80) {
            this.progressColor = UIConstants.SUCCESS;
        } else if (progress >= 60) {
            this.progressColor = UIConstants.WARNING;
        } else {
            this.progressColor = UIConstants.ERROR;
        }
        repaint();
    }

    public int getProgress() {
        return progress;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        repaint();
    }

    public void setProgressColor(Color progressColor) {
        this.progressColor = progressColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight());
        int padding = strokeWidth + 4;
        int diameter = size - (padding * 2);
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;

        // Draw track circle
        g2.setColor(trackColor);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawOval(x, y, diameter, diameter);

        // Draw progress arc (start from 12 o'clock = 90 degrees, clockwise = negative sweep)
        if (progress > 0) {
            g2.setColor(progressColor);
            int sweepAngle = (int) Math.round(-(progress / 100.0) * 360);
            g2.drawArc(x, y, diameter, diameter, 90, sweepAngle);
        }

        // Draw Center Percentage Text
        String percentText = progress + "%";
        g2.setFont(UIConstants.getFont(java.awt.Font.BOLD, 34f));
        g2.setColor(UIConstants.TEXT_MAIN);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(percentText);
        int textX = (getWidth() - textWidth) / 2;
        int textY = (getHeight() / 2) + 2;
        g2.drawString(percentText, textX, textY);

        // Draw Subtitle below percentage
        if (subtitle != null && !subtitle.isEmpty()) {
            g2.setFont(UIConstants.FONT_CAPTION);
            g2.setColor(UIConstants.TEXT_MUTED);
            FontMetrics subFm = g2.getFontMetrics();
            int subWidth = subFm.stringWidth(subtitle);
            int subX = (getWidth() - subWidth) / 2;
            int subY = textY + fm.getDescent() + subFm.getAscent() + 2;
            g2.drawString(subtitle, subX, subY);
        }

        g2.dispose();
    }
}

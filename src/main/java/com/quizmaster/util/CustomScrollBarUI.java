package com.quizmaster.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Modern slim scrollbar UI with rounded thumb and seamless dark background.
 */
public class CustomScrollBarUI extends BasicScrollBarUI {
    private static final int THUMB_SIZE = 8;
    private final Color trackColor = UIConstants.BG_DARK;
    private final Color thumbColor = new Color(0x32, 0x44, 0x68);
    private final Color thumbHoverColor = UIConstants.ACCENT_PRIMARY;

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        return button;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return (scrollbar.getOrientation() == javax.swing.JScrollBar.VERTICAL)
                ? new Dimension(THUMB_SIZE, 48)
                : new Dimension(48, THUMB_SIZE);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(trackColor);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean isHovered = isThumbRollover();
        g2.setColor(isHovered ? thumbHoverColor : thumbColor);

        int arc = THUMB_SIZE;
        g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 1,
                thumbBounds.width - 2, thumbBounds.height - 2, arc, arc);

        g2.dispose();
    }
}

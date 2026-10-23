package com.quizmaster;

import com.quizmaster.util.Theme;
import com.quizmaster.view.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Entry point for QuizMaster Desktop Application.
 */
public class Main {

    public static void main(String[] args) {
        // Ensure UI is constructed on Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize modern dark theme tokens and component look-and-feel
                Theme.applyGlobalTheme();

                // Create and present the main desktop frame
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

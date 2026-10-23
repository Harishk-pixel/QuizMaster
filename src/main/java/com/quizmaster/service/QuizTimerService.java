package com.quizmaster.service;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Service managing the quiz countdown timer, warning thresholds,
 * tick broadcasts, and auto-submission on expiration.
 */
public class QuizTimerService {

    public static final int DEFAULT_SECONDS_PER_QUESTION = 30;
    public static final int WARNING_THRESHOLD_SECONDS = 60;

    public interface TimerTickListener {
        void onTick(int remainingSeconds, String formattedTime, boolean isWarning);
    }

    public interface TimerTimeoutListener {
        void onTimeout();
    }

    private Timer swingTimer;
    private int totalDurationSeconds = 0;
    private int remainingSeconds = 0;
    private boolean running = false;

    private final List<TimerTickListener> tickListeners = new ArrayList<>();
    private final List<TimerTimeoutListener> timeoutListeners = new ArrayList<>();

    public QuizTimerService() {
    }

    public synchronized void start(int totalSeconds) {
        stop(); // Ensure any previously active timer is stopped immediately

        this.totalDurationSeconds = Math.max(0, totalSeconds);
        this.remainingSeconds = this.totalDurationSeconds;
        this.running = true;

        // Broadcast initial time
        broadcastTick();

        if (this.remainingSeconds <= 0) {
            handleTimeout();
            return;
        }

        swingTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tickOneSecond();
            }
        });
        swingTimer.setRepeats(true);
        swingTimer.start();
    }

    public synchronized void stop() {
        if (swingTimer != null) {
            swingTimer.stop();
            swingTimer = null;
        }
        running = false;
    }

    /**
     * Decrements the timer by one second and notifies listeners.
     * Exposed for both Swing timer callbacks and deterministic automated unit testing.
     */
    public synchronized void tickOneSecond() {
        if (!running) {
            return;
        }

        if (remainingSeconds > 0) {
            remainingSeconds--;
            broadcastTick();

            if (remainingSeconds == 0) {
                handleTimeout();
            }
        }
    }

    /**
     * Instantly forces timer expiration for testing auto-submission.
     */
    public synchronized void expireNow() {
        if (!running) return;
        remainingSeconds = 0;
        broadcastTick();
        handleTimeout();
    }

    private void handleTimeout() {
        stop();
        broadcastTimeout();
    }

    private void broadcastTick() {
        String formatted = getFormattedRemainingTime();
        boolean warning = isWarning();
        for (TimerTickListener listener : tickListeners) {
            listener.onTick(remainingSeconds, formatted, warning);
        }
    }

    private void broadcastTimeout() {
        for (TimerTimeoutListener listener : timeoutListeners) {
            listener.onTimeout();
        }
    }

    public String getFormattedRemainingTime() {
        int safe = Math.max(0, remainingSeconds);
        int mins = safe / 60;
        int secs = safe % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    public String getFormattedElapsedTime() {
        int elapsed = getElapsedSeconds();
        int mins = elapsed / 60;
        int secs = elapsed % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    public int getElapsedSeconds() {
        return Math.max(0, totalDurationSeconds - remainingSeconds);
    }

    public int getRemainingSeconds() {
        return Math.max(0, remainingSeconds);
    }

    public int getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public boolean isWarning() {
        return remainingSeconds <= WARNING_THRESHOLD_SECONDS && remainingSeconds > 0;
    }

    public boolean isRunning() {
        return running;
    }

    public void addTickListener(TimerTickListener listener) {
        if (listener != null && !tickListeners.contains(listener)) {
            tickListeners.add(listener);
        }
    }

    public void removeTickListener(TimerTickListener listener) {
        tickListeners.remove(listener);
    }

    public void addTimeoutListener(TimerTimeoutListener listener) {
        if (listener != null && !timeoutListeners.contains(listener)) {
            timeoutListeners.add(listener);
        }
    }

    public void removeTimeoutListener(TimerTimeoutListener listener) {
        timeoutListeners.remove(listener);
    }

    public void clearListeners() {
        tickListeners.clear();
        timeoutListeners.clear();
    }
}

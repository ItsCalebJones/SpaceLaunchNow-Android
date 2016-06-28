package me.calebjones.spacelaunchnow.utils;

import android.os.SystemClock;

/**
 * Measures elapsed time in milliseconds
 */
public class Stopwatch {

    private long startThreadMillis;
    private long startRealtimeMillis;
    private long startUptimeMillis;

    /**
     * Result of Stopwatch.getElapsedTime()
     */
    public static class ElapsedTime {
        private final long elapsedThreadMillis;
        private final long elapsedRealtimeMillis;
        private final long elapsedUptimeMillis;

        /**
         * Constructor
         *
         * @param stopwatch
         *            instance from which to calculate elapsed time
         */
        public ElapsedTime(Stopwatch stopwatch) {
            elapsedThreadMillis = SystemClock.currentThreadTimeMillis() - stopwatch.startThreadMillis;
            elapsedRealtimeMillis = SystemClock.elapsedRealtime() - stopwatch.startRealtimeMillis;
            elapsedUptimeMillis = SystemClock.uptimeMillis() - stopwatch.startUptimeMillis;
        }

        /**
         * Get milliseconds running in current thread
         *
         * This result is only valid if Stopwatch.getElapsedTime() is called from the same
         * thread as the Stopwatch constructor, or the last call to Stopwatch.reset().
         *
         * @return milliseconds
         */
        public long getElapsedThreadMillis() {
            return elapsedThreadMillis;
        }

        /**
         * Get elapsed milliseconds, including time spent in sleep
         *
         * @return milliseconds
         */
        public long getElapsedRealtimeMillis() {
            return elapsedRealtimeMillis;
        }

        /**
         * Get elapsed milliseconds, not counting time spent in deep sleep
         *
         * @return milliseconds
         */
        public long getElapsedUptimeMillis() {
            return elapsedUptimeMillis;
        }

        @Override
        public String toString() {
            return "realtime: " + elapsedRealtimeMillis
                    + " ms; uptime: " + elapsedUptimeMillis
                    + " ms; thread: " + elapsedThreadMillis + " ms";
        }
    }

    /**
     * Constructor
     */
    public Stopwatch() {
        reset();
    }

    /**
     * Set stopwatch's start time to the current time
     */
    public void reset() {
        startThreadMillis = SystemClock.currentThreadTimeMillis();
        startRealtimeMillis = SystemClock.elapsedRealtime();
        startUptimeMillis = SystemClock.uptimeMillis();
    }

    /**
     * Get elapsed time since construction or last call to reset()
     *
     * @return Stopwatch.ElapsedTime
     */
    public ElapsedTime getElapsedTime() {
        return new ElapsedTime(this);
    }

    /**
     * Get elapsed time as a human-readable string
     *
     * If time is less than one second, it will be rendered as a number of milliseconds.
     * Otherwise, it will be rendered as a number of seconds.
     *
     * @return String
     */
    public String getElapsedTimeString() {
        double seconds = (double)getElapsedTime().getElapsedRealtimeMillis() / 1000.0;
        if (seconds < 1.0) {
            return String.format("%.0f ms", seconds * 1000);
        }
        else {
            return String.format("%.2f s", seconds);
        }
    }

    @Override
    public String toString() {
        return "Stopwatch: " + getElapsedTimeString();
    }
}
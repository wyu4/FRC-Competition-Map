package com.FRCCompetitionMap.Gui;

import javax.swing.JComponent;

/**
 * Handles smooth movements of JComponents.
 */
public abstract class AnimationHandler {
    private static int DELTA = 1000/30;

    /**
     * Set the time between each frame in milliseconds.
     * @param delta Milliseconds
     */
    public static void setDelta(int delta) {
        DELTA = delta;
    }

    public static class AnimationJob {
        private final JComponent component;
        private final long startTime;

        public AnimationJob(JComponent component) {
            this.component = component;

            startTime = System.currentTimeMillis();
        }

        public long timeElapsed() {
            return System.currentTimeMillis() - startTime;
        }

        public void tick() {

        }
    }
}

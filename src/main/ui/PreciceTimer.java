package ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PreciceTimer extends Timer {
    public PreciceTimer(int ticksPerSecond, IntervalListener listener) {
        super(1000 / ticksPerSecond,
                new IntervalHandler(listener, 1000 / ticksPerSecond));
    }

    private IntervalHandler getIntervalHandler() {
        return (IntervalHandler) getActionListeners()[0];
    }

    @Override
    public void start() {
        getIntervalHandler().restart();
        super.start();
    }

    public interface IntervalListener {
        void tick(long milliseconds);
    }

    private static class IntervalHandler implements ActionListener {
        private final IntervalListener listener;
        private final int estimatedMillisecondDelay;

        private long prevMilliseconds;
        private long exactMillisecondDiff;

        public IntervalHandler(IntervalListener listener, int estimatedMillisecondDelay) {
            this.listener = listener;
            this.estimatedMillisecondDelay = estimatedMillisecondDelay;
            restart();
        }

        public void restart() {
            exactMillisecondDiff = estimatedMillisecondDelay;
            prevMilliseconds = System.currentTimeMillis();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            listener.tick(exactMillisecondDiff);
            exactMillisecondDiff = System.currentTimeMillis() - prevMilliseconds;
            prevMilliseconds = System.currentTimeMillis();
        }
    }
}

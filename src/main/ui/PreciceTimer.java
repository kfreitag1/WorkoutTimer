package ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// An extension of the Swing Timer class that keeps track of the exact delay between
// successive ticks to improve timing accuracy
public class PreciceTimer extends Timer {

    // EFFECTS: Constructs a PreciceTimer object with the specified ticks per second
    //          and callback class with tick method.
    public PreciceTimer(int ticksPerSecond, IntervalListener listener) {
        super(1000 / ticksPerSecond,
                new IntervalHandler(listener, 1000 / ticksPerSecond));
    }

    // EFFECTS: Returns the IntervalHandler object stored in the Timer superclass.
    private IntervalHandler getIntervalHandler() {
        // Can cast directly from first entry of the array because it should be the only
        // one, and always be initialized.
        return (IntervalHandler) getActionListeners()[0];
    }

    // MODIFIES: this
    // EFFECTS: Calls the super class to start the timer, but first reset the IntervalHandler
    //          to make sure that it starts off properly.
    @Override
    public void start() {
        getIntervalHandler().restart();
        super.start();
    }

    // Objects which implement IntervalListener can be passed into the constructor for
    // PreciceTimer and will have their tick method called on every interval.
    public interface IntervalListener {
        void tick(long milliseconds);
    }

    // Internal "middle man" class to call the tick method on the provided IntervalListener
    // with precise millisecond delays. Keeps track of the system time on every iteration.
    private static class IntervalHandler implements ActionListener {
        private final IntervalListener listener;
        private final int estimatedMillisecondDelay;

        private long prevMilliseconds;
        private long exactMillisecondDiff;

        // EFFECTS: Constructs an IntervalHandler object with the provided listener and a
        //          target for what the millisecond delay should be.
        public IntervalHandler(IntervalListener listener, int estimatedMillisecondDelay) {
            this.listener = listener;
            this.estimatedMillisecondDelay = estimatedMillisecondDelay;
            restart();
        }

        // MODIFIES: this
        // EFFECTS: Restarts the timer.
        public void restart() {
            exactMillisecondDiff = estimatedMillisecondDelay;
            prevMilliseconds = System.currentTimeMillis();
        }

        // MODIFIES: this
        // EFFECTS: Called upon every iteration of the timer, calls the tick method of the listener.
        //          Keeps track of the current system time for the next iteration of this method.
        @Override
        public void actionPerformed(ActionEvent e) {
            listener.tick(exactMillisecondDiff);
            exactMillisecondDiff = System.currentTimeMillis() - prevMilliseconds;
            prevMilliseconds = System.currentTimeMillis();
        }
    }
}

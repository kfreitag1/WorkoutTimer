package ui.screens;

import model.Routine;
import ui.PreciceTimer;
import ui.WorkoutTimerApp;
import ui.components.InfoDisplay;
import ui.components.RoutineDisplay;
import ui.components.RoutineToolbar;
import ui.components.ToolbarButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoutineScreen extends Screen {
    private final Routine routine;
    private String state; // one of "default" "running" "editing" ...
    private Timer timer;

    private RoutineToolbar routineToolbar;
    private RoutineDisplay routineDisplay;
    private InfoDisplay infoDisplay;

    public RoutineScreen(WorkoutTimerApp app, Routine routine) {
        super(app);
        this.routine = routine;
        this.state = "default";

        // Set up routine timer for precise interval
        timer = new PreciceTimer(WorkoutTimerApp.TICKS_PER_SECOND, new TimerHandler());

        initLayout();
    }

    private void initLayout() {
        // Top area - routine name and toolbar
        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.PAGE_AXIS));
        JLabel routineTitle = new JLabel(routine.getName());
        routineTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        routineToolbar = new RoutineToolbar(this);
        topArea.add(routineTitle);
        topArea.add(routineToolbar);
        add(topArea, BorderLayout.NORTH);

        // Center area - routine content
        routineDisplay = new RoutineDisplay(routine);
        add(routineDisplay, BorderLayout.CENTER);

        // Bottom text - info display
        infoDisplay = new InfoDisplay();
        add(infoDisplay, BorderLayout.SOUTH);
    }

    //TODO: changes state, returns true if successful, updates display, handles timer
    public boolean changeState(String newState) {
        switch (newState) {
            case "default":
                if (state.equals("running")) {
                    setState("default");
                    timer.stop();
                    return true;
                }
                break;
            case "running":
                if (state.equals("default")) {
                    setState("running");
                    timer.start();
                    return true;
                }
                break;
            case "editing":
                break;
        }
        return false;
    }

    // newState is one of "default" "running" "editing", and is validated to be okay to change to
    private void setState(String newState) {
        state = newState;
        routineToolbar.updateToState(newState);
    }

    public String getState() {
        return state;
    }

    public void close() {
        app.closeRoutine();
    }

    public void reset() {
        routine.reset();
        routineDisplay.refresh();
    }

    private class TimerHandler implements PreciceTimer.IntervalListener {

        @Override
        public void tick(long milliseconds) {
            if (state.equals("running")) {
                routine.advance(milliseconds);
                routineDisplay.refresh();
            }
        }
    }
}

package ui.screens;

import model.Routine;
import ui.PreciceTimer;
import ui.WorkoutTimerApp;
import ui.components.InfoDisplay;
import ui.components.RoutineDisplay;
import ui.components.RoutineToolbar;
import ui.handlers.SpacebarHandler;

import javax.swing.*;
import java.awt.*;

public class RoutineScreen extends Screen {
    private final Routine routine;
    private String state; // one of "default" "running" "editing" ...
    private final Timer timer;

    private RoutineToolbar routineToolbar;
    private RoutineDisplay routineDisplay;
    private InfoDisplay infoDisplay;

    // --------------------------------------------------------------------------------------------
    // Constructor + helpers
    // --------------------------------------------------------------------------------------------

    public RoutineScreen(WorkoutTimerApp app, Routine routine) {
        super(app);
        this.routine = routine;
        this.state = "default";

        // Set up routine timer for precise interval
        timer = new PreciceTimer(WorkoutTimerApp.TICKS_PER_SECOND, milliseconds -> {
            if (state.equals("running")) {
                routine.advance(milliseconds);
                routineDisplay.refresh();
            }
        });

        initLayout();
        initHandlers();
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

    private void initHandlers() {
        // KeyBinding for spacebar
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(' '), "space");
        getActionMap().put("space", new SpacebarHandler(this));
    }

    // --------------------------------------------------------------------------------------------
    // State methods
    // --------------------------------------------------------------------------------------------

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

    // --------------------------------------------------------------------------------------------
    // Public routine manipulation methods
    // --------------------------------------------------------------------------------------------

    public void resetRoutine() {
        routine.reset();
        routineDisplay.refresh();
    }

    public void advanceRoutineManual() {
        if (state.equals("running")) {
            routine.advance();
            routineDisplay.refresh();
        }
    }

    // --------------------------------------------------------------------------------------------
    // Public methods
    // --------------------------------------------------------------------------------------------

    public void close() {
        app.closeRoutine();
    }
}

package ui.screens;

import model.ManualSegment;
import model.Routine;
import model.Segment;
import ui.PreciceTimer;
import ui.WorkoutTimerApp;
import ui.components.InfoDisplay;
import ui.components.RoutineDisplay;
import ui.components.RoutineToolbar;
import ui.handlers.EscapeHandler;
import ui.handlers.SegmentMouseHandler;
import ui.handlers.SpacebarHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class RoutineScreen extends Screen {
    private final Routine routine;
    private String state; // one of "default" "running" "editing" "adding" "deleting" ...
    private final Timer timer;

    private RoutineToolbar routineToolbar;
    private RoutineDisplay routineDisplay;
    private InfoDisplay infoDisplay;

    // only set when the user clicks the add segment button and makes a segment
    private Segment constructedSegment = null;

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
                refresh();
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
        routineDisplay = new RoutineDisplay(routine, state, new SegmentMouseHandler(this));
        add(routineDisplay, BorderLayout.CENTER);

        // Bottom text - info display
        infoDisplay = new InfoDisplay();
        add(infoDisplay, BorderLayout.SOUTH);
    }

    private void initHandlers() {
        // KeyBinding for spacebar
        KeyStroke spaceKey = KeyStroke.getKeyStroke(' ');
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(spaceKey, "space");
        getActionMap().put("space", new SpacebarHandler(this));

        // KeyBinding for escape key
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKey, "escape");
        getActionMap().put("escape", new EscapeHandler(this));
    }

    // --------------------------------------------------------------------------------------------
    // State methods
    // --------------------------------------------------------------------------------------------

    //TODO: changes state if valid
    public boolean changeState(String newState) {

        // Return false if the state to change to is invalid from the current state
        switch (newState) {
            case "default": // Can always enter default state
                break;
            case "running": // Can only go into the these states from the default state
            case "deleting":
            case "adding":
            case "editing":
                if (!state.equals("default")) {
                    return false;
                }
                break;
            default:
                throw new IllegalStateException("RoutineScreen state was not an expected value");
        }

        // Should be validated to change to state now
        setState(newState);
        return true;
    }

    // newState is one of "default" "running" "editing" "deleting" "adding"
    // and is validated to be okay to change to
    private void setState(String newState) {
        state = newState;
        routineToolbar.updateToState(newState);
        refresh();

        switch (newState) {
            case "default":
                timer.stop();
                infoDisplay.clear();
                break;
            case "running":
                timer.start();
                break;
            case "adding":
                makeNewSegment();
                break;
            case "deleting":
                infoDisplay.displayMessage("Choose a segment to delete");
                break;
            case "editing":
                infoDisplay.displayMessage("Choose a segment to edit");
                break;
        }
    }

    public String getState() {
        return state;
    }

    // --------------------------------------------------------------------------------------------
    // Private routine manipulation methods
    // --------------------------------------------------------------------------------------------

    private void beginEditSegment(Segment segment) {
        // TODO
    }

    private void makeNewSegment() {
        // TODO - new panel to make new segment and put it into constructedSegment
        constructedSegment = new ManualSegment("CONSTRUCT"); // TODO remove

        if (routine.getSegments().isEmpty()) {
            // If there are no segments, just insert it directly
            addPremadeSegment(null, false);
        } else {
            // Otherwise wait for the user to choose a location
            infoDisplay.displayMessage("Choose a location to insert new segment");
        }
    }

    // requires that
    // if segmentToInsertAround is null then insert at start of list
    private void addPremadeSegment(Segment segmentToInsertAround, boolean insertBefore) {
        Segment segment = new ManualSegment("TEST"); // TODO remove

        if (segmentToInsertAround == null) {
            routine.addSegment(segment);
        } else if (insertBefore) {
            routine.insertSegmentBefore(segment, segmentToInsertAround);
        } else {
            routine.insertSegmentAfter(segment, segmentToInsertAround);
        }

        setState("default");
        refresh();
    }

    private void deleteSegment(Segment segment) {
        routine.removeSegment(segment);
        setState("default");
    }

    // --------------------------------------------------------------------------------------------
    // Public routine manipulation methods
    // --------------------------------------------------------------------------------------------

    public void resetRoutine() {
        routine.reset();
        refresh();
    }

    public void advanceRoutineManual() {
        if (state.equals("running")) {
            routine.advance();
            refresh();
        }
    }

    // --------------------------------------------------------------------------------------------
    // Public methods
    // --------------------------------------------------------------------------------------------

    public void clickedSegmentLocation(Segment segment, boolean topHalf) {
        System.out.println(segment.getName() + " " + topHalf);

        switch (state) {
            case "editing":
                beginEditSegment(segment);
                break;
            case "adding":
                addPremadeSegment(segment, topHalf);
                break;
            case "deleting":
                deleteSegment(segment);
                break;
        }
    }

    public void refresh() {
        routineDisplay.refresh(state);
    }

    public void close() {
        app.closeRoutine();
    }
}

package ui.screens;

import model.Routine;
import model.Segment;
import persistence.RoutineWriter;
import ui.PreciceTimer;
import ui.WorkoutTimerApp;
import ui.components.routine.InfoDisplay;
import ui.components.routine.RoutineDisplay;
import ui.components.routine.RoutineToolbar;
import ui.handlers.SegmentMouseHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

// Represents the routine screen of the Workout Timer application.
// Centered around one Routine object which contains all the information to be shown.
public class RoutineScreen extends Screen {
    private final Routine routine;
    private final Timer timer;

    private String state; // one of "default" "running" "editing" "adding" "deleting" ...

    private RoutineToolbar routineToolbar;
    private RoutineDisplay routineDisplay;
    private InfoDisplay infoDisplay;

    // only set when the user clicks the add segment button and makes a segment
    private Segment constructedSegment = null;

    // --------------------------------------------------------------------------------------------
    // Constructor + helpers
    // --------------------------------------------------------------------------------------------

    // EFFECTS: Constructs a new routine screen on the given app,
    //          with the given Routine object
    public RoutineScreen(WorkoutTimerApp app, Routine routine) {
        super(app);
        this.routine = routine;
        this.state = "default";

        // Set up routine timer for precise interval
        timer = new PreciceTimer(WorkoutTimerApp.TICKS_PER_SECOND, milliseconds -> {
            if (state.equals("running")) {
                routine.advance(milliseconds);
                refresh();

                // Check if complete
                if (routine.isComplete()) {
                    infoDisplay.displaySuccess("Routine complete!");
                } else {
                    infoDisplay.clear();
                }
            }
        });

        initLayout();
        initKeyBindings();
    }

    // MODIFIES: this
    // EFFECTS: Lays out all the elements on the screen
    private void initLayout() {

        // Top area - routine name and toolbar
        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.PAGE_AXIS));

        // Wrap title in a container to make it horizontally centered
        JLabel routineTitle = new JLabel(routine.getName());
        routineTitle.setFont(new Font("Sans-Serif", Font.BOLD, 20));
        JPanel titleContainer = new JPanel();
        titleContainer.add(routineTitle);
        titleContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        routineToolbar = new RoutineToolbar(this);

        topArea.add(titleContainer);
        topArea.add(routineToolbar);
        add(topArea, BorderLayout.NORTH);

        // Center area - routine content
        routineDisplay = new RoutineDisplay(routine, state, new SegmentMouseHandler(this));
        add(routineDisplay, BorderLayout.CENTER);

        // Bottom text - info display
        infoDisplay = new InfoDisplay();
        add(infoDisplay, BorderLayout.SOUTH);
    }

    // MODIFIES: this
    // EFFECTS: Attaches any keybindings to the screen, i.e. functions that are called
    //          whenever a certain key is pressed.
    private void initKeyBindings() {
        // When spacebar is pressed, try to advance any manual segments
        KeyStroke spaceKey = KeyStroke.getKeyStroke(' ');
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(spaceKey, "space");
        getActionMap().put("space", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                advanceRoutineManual();
            }
        });

        // When escape key is pressed, try to change to the default state
        KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKey, "escape");
        getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeState("default");
            }
        });
    }

    // --------------------------------------------------------------------------------------------
    // State methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Changes the state if it is a valid transition based on the current state,
    //          returns true if the change to the new state was successful.
    public boolean changeState(String newState) {
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

    // REQUIRES: newState is one of "default" "running" "editing" "deleting" "adding"
    //           and is validated to be okay to change to.
    // MODIFIES: this
    // EFFECTS: Internal function to set the state to the new state and perform any
    //          necessary updates to layouts or other objects. Notably, starts and stops
    //          the timer used to increment time in the routine when running.
    private void setState(String newState) {
        state = newState;
        routineToolbar.updateToState(newState);
        refresh();
        infoDisplay.clear();

        switch (newState) {
            case "default":
                timer.stop();
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

    // MODIFIES: this
    // EFFECTS: Starts the editing dialog for a certain segment in the routine
    private void beginEditSegment(Segment segmentToEdit) {
        new AddEditDialog(app, segmentToEdit);
        changeState("default");
    }

    // MODIFIES: this
    // EFFECTS: Starts the adding dialog to make a new segment, then either adds it to
    //          the routine (if routine is empty), otherwise waits for the user to click
    //          a location to add the segment.
    private void makeNewSegment() {
        // Reset the global storage for the constructed segment
        constructedSegment = null;

        // Present add dialog and set the resulting segment to the constructed segment object
        new AddEditDialog(app, newSegment -> constructedSegment = newSegment);

        // User cancelled out of making the new segment
        if (constructedSegment == null) {
            changeState("default");
            return;
        }

        if (routine.getSegments().isEmpty()) {
            // If there are no segments, just insert it directly into the routine
            addPremadeSegment(null, false);
        } else {
            // Otherwise wait for the user to choose a location
            infoDisplay.displayMessage("Choose a location to insert new segment");
        }
    }

    // REQUIRES: this.constructedSegment is not null, segmentToInsertAround is in routine
    // MODIFIES: this
    // EFFECTS: Adds the segment constructed by the user to the routine in a relative
    //          position around another segment in the routine. If segmentToInsertAround is null
    //          then just inserts the new segment into the first position on the routine.
    private void addPremadeSegment(Segment segmentToInsertAround, boolean insertBefore) {
        assert (constructedSegment != null);

        if (segmentToInsertAround == null) {
            routine.addSegment(constructedSegment);
        } else if (insertBefore) {
            routine.insertSegmentBefore(constructedSegment, segmentToInsertAround);
        } else {
            routine.insertSegmentAfter(constructedSegment, segmentToInsertAround);
        }

        changeState("default");
    }

    // REQUIRES: segment is in routine
    // MODIFIES: this
    // EFFECTS: Removes the segment from the routine
    private void deleteSegment(Segment segment) {
        routine.removeSegment(segment);
        changeState("default");
    }

    // --------------------------------------------------------------------------------------------
    // Public routine manipulation methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Resets the routine to have all segments incomplete
    public void resetRoutine() {
        routine.reset();
        refresh();
    }

    // MODIFIES: this
    // EFFECTS: Advances the routine if it is running and on a manual segment
    public void advanceRoutineManual() {
        if (state.equals("running")) {
            routine.advance();
            refresh();
        }
    }

    // --------------------------------------------------------------------------------------------
    // Public methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Performs the desired action when the user selects a segment or segment
    //          location, from a choosing state (i.e. deleting, editing, adding).
    public void clickedSegmentLocation(Segment segment, boolean topHalf) {
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

    // MODIFIES: this
    // EFFECTS: Refreshes the routine display to update any changes
    public void refresh() {
        routineDisplay.refresh(state);
    }

    // MODIFIES: this
    // EFFECTS: Closes the routine and returns to the main menu, first asking if
    //          the user wants to save the routine.
    public void close() {
        int answer = JOptionPane.showConfirmDialog(
                this, "Would you like to save?",
                "Save routine",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (answer == JOptionPane.YES_OPTION) {
            save();
        }

        app.closeRoutine();
    }

    // MODIFIES: this
    // EFFECTS: Saves the routine to the filesystem
    public void save() {
        // TODO: make more robust, don't assume the filename is the same as routine name
        Path pathname = Paths.get("data", "savedroutines", routine.getName() + ".json");
        RoutineWriter routineWriter = new RoutineWriter(pathname.toString());

        try {
            routineWriter.open();
            routineWriter.write(routine);
            routineWriter.close();

            infoDisplay.displaySuccess("Saved!");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error in saving file!");
        }
    }
}

package ui.handlers;

import ui.screens.RoutineScreen;

import java.awt.event.ActionEvent;

// Represents a handler for the play/pause button
public class PlayPauseButtonHandler extends RoutineHandler {
    public PlayPauseButtonHandler(RoutineScreen parentRoutineScreen) {
        super(parentRoutineScreen);
    }

    // MODIFIES: parentRoutineScreen
    // EFFECTS: Sets the state of the routine screen depending on if the user
    //          clicked pause (currently in running state), or play (currently in
    //          default state)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (parentRoutineScreen.getState().equals("default")) {
            // Play
            parentRoutineScreen.changeState("running");
        } else if (parentRoutineScreen.getState().equals("running")) {
            // Pause
            parentRoutineScreen.changeState("default");
        }
    }
}

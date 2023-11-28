package ui.handlers;

import ui.screens.RoutineScreen;
import ui.screens.RoutineScreenState;

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
        switch (parentRoutineScreen.getState()) {
            case DEFAULT:
                parentRoutineScreen.changeState(RoutineScreenState.RUNNING);
                break;
            case RUNNING:
                parentRoutineScreen.changeState(RoutineScreenState.DEFAULT);
                break;
        }
    }
}

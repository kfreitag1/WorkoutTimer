package ui.handlers;

import ui.screens.RoutineScreen;

import java.awt.event.ActionEvent;
import java.util.Objects;

public class PlayPauseButtonHandler extends ToolbarActionHandler {

    public PlayPauseButtonHandler(RoutineScreen parentRoutineScreen) {
        super(parentRoutineScreen);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (parentRoutineScreen.getState().equals("default")) {
            // play
            parentRoutineScreen.changeState("running");
        } else if (parentRoutineScreen.getState().equals("running")) {
            // pause
            parentRoutineScreen.changeState("default");
        }
    }
}

package ui.handlers;

import ui.screens.RoutineScreen;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SpacebarHandler extends AbstractAction  {
    private final RoutineScreen parentRoutineScreen;

    public SpacebarHandler(RoutineScreen parentRoutineScreen) {
        super();
        this.parentRoutineScreen = parentRoutineScreen;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        parentRoutineScreen.advanceRoutineManual();
    }
}

package ui.handlers;

import ui.screens.RoutineScreen;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SpacebarHandler extends RoutineHandler  {
    public SpacebarHandler(RoutineScreen parentRoutineScreen) {
        super(parentRoutineScreen);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        parentRoutineScreen.advanceRoutineManual();
    }
}

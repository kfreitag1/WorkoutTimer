package ui.handlers;

import ui.screens.RoutineScreen;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EscapeHandler extends RoutineHandler  {
    public EscapeHandler(RoutineScreen parentRoutineScreen) {
        super(parentRoutineScreen);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        parentRoutineScreen.changeState("default");
    }
}

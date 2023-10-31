package ui.handlers;

import ui.screens.RoutineScreen;

import java.awt.event.ActionListener;

public abstract class ToolbarActionHandler implements ActionListener {
    protected final RoutineScreen parentRoutineScreen;

    public ToolbarActionHandler(RoutineScreen parentRoutineScreen) {
        this.parentRoutineScreen = parentRoutineScreen;
    }
}

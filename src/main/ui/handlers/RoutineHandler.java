package ui.handlers;

import ui.screens.RoutineScreen;

import java.awt.event.ActionListener;

public abstract class RoutineHandler {
    protected final RoutineScreen parentRoutineScreen;

    public RoutineHandler(RoutineScreen parentRoutineScreen) {
        this.parentRoutineScreen = parentRoutineScreen;
    }
}

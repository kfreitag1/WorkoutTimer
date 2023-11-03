package ui.handlers;

import ui.screens.RoutineScreen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Represents an abstract handler for all event handlers that need to
// maintain a reference to the RoutineScreen object to pass information back
public abstract class RoutineHandler extends AbstractAction {
    protected final RoutineScreen parentRoutineScreen;

    // EFFECTS: Constructs generic routine handler with the given RoutineScreen to store
    public RoutineHandler(RoutineScreen parentRoutineScreen) {
        this.parentRoutineScreen = parentRoutineScreen;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // No default action
    }
}

package ui.screens;

import ui.WorkoutTimerApp;

import javax.swing.*;
import java.awt.*;

// Represents a generic screen to be used in the Workout Timer application.
// Keeps a reference to the WorkoutTimerApp instance
public abstract class Screen extends JPanel {
    protected final WorkoutTimerApp app;

    // EFFECTS: Constructs a new Workout Timer screen
    public Screen(WorkoutTimerApp app) {
        super(new BorderLayout());
        this.app = app;
    }
}

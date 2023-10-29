package ui.screens;

import ui.WorkoutTimerApp;

import javax.swing.*;

public abstract class Screen extends JPanel {
    protected final WorkoutTimerApp app;

    public Screen(WorkoutTimerApp app) {
        super();
        this.app = app;
    }
}

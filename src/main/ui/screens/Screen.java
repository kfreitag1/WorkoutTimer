package ui.screens;

import ui.WorkoutTimerApp;

import javax.swing.*;
import java.awt.*;

public abstract class Screen extends JPanel {
    protected final WorkoutTimerApp app;

    public Screen(WorkoutTimerApp app) {
        super(new BorderLayout());
        this.app = app;
    }
}

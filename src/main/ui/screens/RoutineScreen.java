package ui.screens;

import model.Routine;
import ui.WorkoutTimerApp;

import javax.swing.*;

public class RoutineScreen extends Screen {
    private Routine routine;

    public RoutineScreen(WorkoutTimerApp app, Routine routine) {
        super(app);
        this.routine = routine;

        // TEMPORARY
        add(new JLabel("routine screen with name: "));
        JButton button = new JButton("close routine");
        button.addActionListener(e -> app.closeRoutine());
        add(button);

    }
}

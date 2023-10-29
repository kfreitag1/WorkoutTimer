package ui.screens;

import ui.WorkoutTimerApp;

import javax.swing.*;

public class MainMenuScreen extends Screen {

    public MainMenuScreen(WorkoutTimerApp app) {
        super(app);

        // TEMPORARY
        add(new JLabel("this is the main menu"));
        JButton button = new JButton("new routine");
        button.addActionListener(e -> {
            app.initRoutine("name");
        });
        add(button);
    }
}

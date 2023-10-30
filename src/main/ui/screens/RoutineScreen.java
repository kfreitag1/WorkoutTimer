package ui.screens;

import model.Routine;
import ui.WorkoutTimerApp;
import ui.components.InfoDisplay;
import ui.components.RoutineDisplay;
import ui.components.RoutineToolbar;
import ui.components.ToolbarButton;

import javax.swing.*;
import java.awt.*;

public class RoutineScreen extends Screen {
    private final Routine routine;

    public RoutineScreen(WorkoutTimerApp app, Routine routine) {
        super(app);
        this.routine = routine;

        // Top area - routine name and toolbar
        JPanel topArea = new JPanel();
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.PAGE_AXIS));
        JLabel routineTitle = new JLabel(routine.getName());
        routineTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        topArea.add(routineTitle);
        topArea.add(new RoutineToolbar());
        add(topArea, BorderLayout.NORTH);

        // Center area - routine content
        add(new RoutineDisplay(routine), BorderLayout.CENTER);

        // Bottom text - info display
        InfoDisplay info = new InfoDisplay();
        info.displayError("this is an error");
        info.displayMessage("this is a message");
        info.clear();
        add(info, BorderLayout.SOUTH);
    }
}

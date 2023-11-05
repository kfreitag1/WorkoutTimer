package ui.screens;

import ui.WorkoutTimerApp;
import ui.components.ValidatedTextField;
import ui.components.mainmenu.SavedRoutinesList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

// Represents the main menu screen of the Workout Timer application.
// Allows the user to make a new routine or open a saved routine.
public class MainMenuScreen extends Screen {
    private ValidatedTextField nameEntry;
    private JButton newRoutineButton;

    // EFFECTS: Constructs the main menu screen
    public MainMenuScreen(WorkoutTimerApp app) {
        super(app);

        JPanel topPanel = makeTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = makeCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
    }

    // EFFECTS: Constructs the top panel containing the UI needed to construct a
    //          new routine.
    private JPanel makeTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        topPanel.add(new JLabel("New routine name:"));

        newRoutineButton = new JButton("Create new routine");
        newRoutineButton.addActionListener(e -> app.initRoutine(nameEntry.getText().trim()));
        newRoutineButton.setEnabled(false);

        nameEntry = new ValidatedTextField(".+", this::setStateOfNewRoutineButton, "");

        topPanel.add(nameEntry);
        topPanel.add(newRoutineButton);

        return topPanel;
    }

    // EFFECTS: Constructs the center panel containing the list of saved routines
    private JPanel makeCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));

        JLabel loadLabel = new JLabel("Load a routine:");
        loadLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(loadLabel);

        SavedRoutinesList routineList = new SavedRoutinesList(app::initRoutine);
        routineList.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(routineList);

        return centerPanel;
    }

    // MODIFIES: this
    // EFFECTS: Sets the new routine button to be turned on ONLY when a valid
    //          name is inputted into the new routine name entry field.
    private void setStateOfNewRoutineButton() {
        newRoutineButton.setEnabled(nameEntry.checkValid());
    }
}

package ui;

import model.ManualSegment;
import model.RepeatSegment;
import model.Routine;
import model.TimeSegment;
import ui.screens.MainMenuScreen;
import ui.screens.RoutineScreen;
import ui.screens.Screen;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class WorkoutTimerApp extends JFrame {
    private static final int MIN_WIDTH = 550;
    private static final int MIN_HEIGHT = 600;

    private Routine currentRoutine = null; // only set when on RoutineScreen

    // --------------------------------------------------------------------------------------------
    // Constructor
    // --------------------------------------------------------------------------------------------

    // EFFECTS: Constructs the window for WorkoutTimer and displays it to the user.
    //          Start out at the main menu screen.
    public WorkoutTimerApp() {
        // Set default parameters of the UI window
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);

        // Sets the first screen to the main menu so the user can make/load a routine
        setScreen(new MainMenuScreen(this));
    }

    // --------------------------------------------------------------------------------------------
    // Public methods (accessed by user input on screens)
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Sets the current routine to the given routine and changes the screen to
    //          the routine screen
    public void initRoutine(Routine routine) {
        currentRoutine = routine;
        setScreen(new RoutineScreen(this, routine));
    }

    // MODIFIES: this
    // EFFECTS: Convenience method for initRoutine to make new routine with given name
    public void initRoutine(String name) {
        /////////////////////////////////////////
        // TODO: REMOVE TEMPORARY TEST ROUTINE
        ////////////////////////////////////////
        Routine testRoutine = new Routine(name);
        testRoutine.addSegment(new TimeSegment("Time1", 10000, 10000));
        testRoutine.addSegment(new RepeatSegment("Repeat1", 2, new ArrayList<>(Arrays.asList(
                new ManualSegment("Manual1", true),
                new RepeatSegment("Repeat2", 4, new ArrayList<>(Arrays.asList(
                        new ManualSegment("Manual2", true),
                        new TimeSegment("Time2", 4000, 2000)
                )), 2),
                new TimeSegment("Time3", 5000)
        ))));

//        initRoutine(new Routine(name));
        initRoutine(testRoutine);
    }

    public void closeRoutine() {
        //TODO: ask the user if they want to save
        currentRoutine = null;
        setScreen(new MainMenuScreen(this));
    }

    // --------------------------------------------------------------------------------------------
    // Private methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Puts the given Screen on the UI window, clears the previous screen.
    private void setScreen(Screen newScreen) {
        getContentPane().removeAll();
        getContentPane().add(newScreen);
        getContentPane().revalidate();
    }
}

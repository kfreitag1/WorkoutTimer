package ui;

import model.Routine;
import ui.screens.MainMenuScreen;
import ui.screens.RoutineScreen;
import ui.screens.Screen;

import javax.swing.*;
import java.awt.*;

// Represents a UI based application for Workout Timer
public class WorkoutTimerApp extends JFrame {
    public static final int TICKS_PER_SECOND = 30;
    private static final int MIN_WIDTH = 520;
    private static final int MIN_HEIGHT = 600;

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
        setScreen(new RoutineScreen(this, routine));
    }

    // MODIFIES: this
    // EFFECTS: Convenience method for initRoutine to make new routine with given name
    public void initRoutine(String name) {
        initRoutine(new Routine(name));
    }

    // MODIFIES: this
    // EFFECTS: Closes the currently opened routine and shows the main menu
    public void closeRoutine() {
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

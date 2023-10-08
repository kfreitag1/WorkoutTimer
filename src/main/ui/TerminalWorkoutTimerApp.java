package ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import model.Routine;
import model.Segment;

import java.io.IOException;
import java.util.List;

public class TerminalWorkoutTimerApp {
    private static final int TICKS_PER_SECOND = 6;

    private Screen screen;
    private TerminalSize terminalSize;
    private String applicationState; // One of: "main_menu", "routine", "running"
    private Routine activeRoutine;
    private String commandPrompt;

    // Public methods
    // --------------------------------------------------------------------------------------------

    // EFFECTS:
    public void start() throws IOException, InterruptedException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        screen = terminalFactory.createScreen();
        screen.startScreen();

        applicationState = "main_menu";

        // Initiate application input/output loop
        applicationLoop();
    }

    // Private application methods
    // --------------------------------------------------------------------------------------------

    // EFFECTS:
    private void applicationLoop() throws IOException, InterruptedException {
        long milliseconds = 1000L / TICKS_PER_SECOND;
        while (tick(milliseconds)) {
            Thread.sleep(milliseconds);
        }
        System.exit(0);
    }

    // advance application by certain number of millis, returns false when the user quits the
    // application
    private boolean tick(long milliseconds) throws IOException {
        boolean keepGoing = handleUserInput();

        if (applicationState.equals("running")) {
            // TODO: update activeRoutine by progressing by milliseconds
        }
        renderScreen();

        return keepGoing;
    }

    // Private user input methods
    // --------------------------------------------------------------------------------------------

    // handles button presses, returns false when the user quits the application
    private boolean handleUserInput() throws IOException {
        KeyStroke stroke = screen.pollInput();
        if (stroke == null) {
            return true;
        }

        switch (applicationState) {
            case "main_menu":
                return handleUserInputMainMenu(stroke);
            case "routine":
                return handleUserInputRoutine(stroke);
            case "running":
                return handleUserInputRunning(stroke);
        }
        return false; // Will never reach here
    }

    // handles input for the main menu application state
    private boolean handleUserInputMainMenu(KeyStroke stroke) {
        return true; // stub
    }

    // handles input for the routine application state
    private boolean handleUserInputRoutine(KeyStroke stroke) {
        return true; // stub
    }

    // handles input for the running application state
    private boolean handleUserInputRunning(KeyStroke stroke) {
        // if current timer segment is manual and keystroke is thing then update segment
        return true; // stub
    }

    // modifies the commandPrompt argument, make sure its always back to null whenever done
    private String getCommandWithRenderDisplay(String prompt) throws IOException {
        // Set prompt to global environment
        this.commandPrompt = prompt;

        // exits when the enter key is pressed
        // string builder because intellij wanted me to
        StringBuilder command = new StringBuilder();
        while (true) {
            KeyStroke stroke = screen.readInput();
            switch (stroke.getKeyType()) {
                case Character:
                    command.append(stroke.getCharacter());
                    renderScreen();
                    break;
                case Enter:
                    this.commandPrompt = null;
                    return command.toString();
            }
        }
    }

    // Private render methods
    // --------------------------------------------------------------------------------------------

    // renders the terminal graphics on the screen and manages screen size changes
    private void renderScreen() throws IOException {
        screen.setCursorPosition(null);
        screen.clear();

        // Update screen size
        if (screen.doResizeIfNecessary() != null) {
            terminalSize = screen.getTerminalSize();
        }

        switch (applicationState) {
            case "main_menu":
                renderMainMenu(screen);
                break;
            case "routine":
                renderRoutine(screen);
                break;
            case "running":
                renderRunning(screen);
                break;
        }

        // TODO: command input stuff here, after all of the other rendering

        screen.refresh();
    }

    // handles input for the main menu application state
    private void renderMainMenu(Screen screen) {
        TextGraphics draw = screen.newTextGraphics();
        draw.putString(0, 0, "press command: (n)ew (l)oad (q)uit");
    }

    // requires that activeRoutine is not null
    // handles input for the routine application state
    private void renderRoutine(Screen screen) {
        TextGraphics draw = screen.newTextGraphics();
        draw.putString(0, 0, "press command: (c)lose (s)ave (p)lay (r)estart (a)dd (d)elete (e)dit");
        draw.putString(1, 0, "Title: " + activeRoutine.getName());

        List<Segment> segments = activeRoutine.getFlattenedSegments();

        for (int i = 0; i < segments.size(); i++) {
            draw.putString(2 + i, 0, segments.get(i).getName());
        }
    }

    // handles input for the running application state
    private void renderRunning(Screen screen) {
        TextGraphics draw = screen.newTextGraphics();
        draw.putString(0, 0, "press command: (p)ause");
    }
}

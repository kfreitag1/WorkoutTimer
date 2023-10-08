package ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
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
    private String commandInput;

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
    private boolean handleUserInputMainMenu(KeyStroke stroke) throws IOException {
        if (!stroke.getKeyType().equals(KeyType.Character)) {
            return true;
        }

        switch (stroke.getCharacter()) {
            case 'n': // new
                String routineName = getCommandWithRenderDisplay("Name of routine: ");
                activeRoutine = new Routine(routineName);
                applicationState = "routine";
                break;
            case 'l': // load
                break;
            case 'q': // quit
                return false;
        }
        return true;
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

    // modifies the commandPrompt, commandInput argument,
    // make sure prompt always back to null whenever done
    private String getCommandWithRenderDisplay(String prompt) throws IOException {
        // Set prompt to global environment
        this.commandPrompt = prompt;
        this.commandInput = "";

        // exits when the enter key is pressed
        // string builder because intellij wanted me to
        while (true) {
            renderScreen();

            KeyStroke stroke = screen.readInput();
            switch (stroke.getKeyType()) {
                case Character:
                    commandInput += stroke.getCharacter();
                    break;
                case Backspace:
                    if (!commandInput.isEmpty()) {
                        commandInput = commandInput.substring(0, commandInput.length() - 1);
                    }
                    break;
                case Enter:
                    commandPrompt = null;
                    return commandInput;
            }
        }
    }

    // Private render methods
    // --------------------------------------------------------------------------------------------

    // renders the terminal graphics on the screen and manages screen size changes
    private void renderScreen() throws IOException {
        screen.setCursorPosition(TerminalPosition.TOP_LEFT_CORNER);
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
        renderCommandPromptAndInput(screen);
        screen.refresh();
    }

    // command prompt can have newline characters
    private void renderCommandPromptAndInput(Screen screen) {
        if (commandPrompt != null) {
            TerminalPosition startPosition = screen.getCursorPosition();
            TextGraphics commandDraw = screen.newTextGraphics();

            // Get length of the last line in the command prompt, and total number of lines
            List<String> splitCommandPrompt = List.of(commandPrompt.split("\n"));
            int numLines = splitCommandPrompt.size();
            int lengthLastLine = splitCommandPrompt.get(splitCommandPrompt.size() - 1).length();

            // Output whole command prompt on multiple lines
            for (int i = 0; i < numLines; i++) {
                TerminalPosition linePosition = startPosition.withRelativeRow(i);
                commandDraw.putString(linePosition, splitCommandPrompt.get(i));
            }

            // Output the user input, and set the cursor appropriately
            TerminalPosition commandPosition = startPosition.withRelative(lengthLastLine, numLines - 1);
            commandDraw.putString(commandPosition, commandInput);
            screen.setCursorPosition(commandPosition.withRelativeColumn(commandInput.length()));
        }
    }

    // handles input for the main menu application state
    // modifies cursor position to be after whatever this is
    private void renderMainMenu(Screen screen) {
        TextGraphics draw = screen.newTextGraphics();
        draw.putString(0, 0, "press command: (n)ew (l)oad (q)uit");

        screen.setCursorPosition(new TerminalPosition(0, 1));
    }

    // requires that activeRoutine is not null
    // handles input for the routine application state
    // modifies cursor position to be after whatever this is
    private void renderRoutine(Screen screen) {
        TextGraphics draw = screen.newTextGraphics();
        draw.putString(0, 0, "press command: (c)lose (s)ave (p)lay (r)estart (a)dd (d)elete (e)dit");
        draw.putString(0, 1, "Title: " + activeRoutine.getName());

        List<Segment> segments = activeRoutine.getFlattenedSegments();

        for (int i = 0; i < segments.size(); i++) {
            draw.putString(0, 2 + i, segments.get(i).getName());
        }

        screen.setCursorPosition(new TerminalPosition(0, 2 + segments.size()));
    }

    // handles input for the running application state
    // modifies cursor position to be after whatever this is
    private void renderRunning(Screen screen) {
        TextGraphics draw = screen.newTextGraphics();
        draw.putString(0, 0, "press command: (p)ause");

        screen.setCursorPosition(new TerminalPosition(0, 1));
    }
}

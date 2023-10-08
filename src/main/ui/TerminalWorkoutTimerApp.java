package ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import model.ManualSegment;
import model.Routine;
import model.Segment;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TerminalWorkoutTimerApp {
    private static final int TICKS_PER_SECOND = 6;

    private Screen screen;

    private String applicationState; // One of: "main_menu", "routine", "running"
    private Routine activeRoutine; // null during the main_menu, set during routine

    // NOT FOR USE BY ANY FUNCTION EXCEPT getCommandWithRenderDisplay, renderCommandPromptAndInput
    private String commandPrompt; // can have newline characters, null unless getting input from user
    private String commandError; // has the error message of the previous input, invalid unless in getCommand function
    private String commandInput; // has the input of the user, invalid unless in getCommand function

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
                newRoutineRoutine();
                break;
            case 'l': // load
                break;
            case 'q': // quit
                return false;
        }
        return true;
    }

    private void newRoutineRoutine() throws IOException {
        String name = getStringWithValidation(
                "Routine name: ",
                "Name cannot be empty",
                "\\S+");

        activeRoutine = new Routine(name);
        applicationState = "routine";
    }

    // handles input for the routine application state
    private boolean handleUserInputRoutine(KeyStroke stroke) throws IOException {
        if (!stroke.getKeyType().equals(KeyType.Character)) {
            return true;
        }

        switch (stroke.getCharacter()) {
            case 'p': // play
                // ensure isComplete = false before going to
                break;
            case 'r': // load
                break;
            case 'a': // add
                addSegmentRoutine();
                break;
            case 'd': // delete
                break;
            case 'e': // edit
                break;
            case 'c': // close
                break;
            case 's': // save
                break;
            case 'q': // quit
                return false;
        }
        return true;
    }

    private void addSegmentRoutine() throws IOException {
        String name = getStringWithValidation(
                "Segment name: ",
                "Name cannot be empty",
                "\\S+");

        String type = getStringWithValidation(
                "Segment type: (t)imed, (r)epeat, (m)anual ",
                "Type is not one of 't', 'r', or 'm'",
                "[trm]");

        Long time = getTimeWithValidation();
    }

    // handles input for the running application state
    private boolean handleUserInputRunning(KeyStroke stroke) {
        if (stroke.getKeyType().equals(KeyType.Character) && stroke.getCharacter().equals('p')) {
            // pauses timer by going back to the routine menu
            applicationState = "routine";
        }

        // if current timer segment is manual and keystroke is thing then update segment
        if (!activeRoutine.isComplete() && activeRoutine.getExactCurrentSegment() instanceof ManualSegment) {
            if (stroke.getKeyType().equals(KeyType.Enter)) {
                ((ManualSegment) activeRoutine.getExactCurrentSegment()).setComplete();
            }
        }

        return true;
    }

    // modifies the commandPrompt, commandInput fields,
    // make sure prompt always back to null whenever done
    private String getCommandWithRenderDisplay(String prompt) throws IOException {
        return getCommandWithRenderDisplay(prompt, "");
    }

    private String getCommandWithRenderDisplay(String prompt, String errorMessage) throws IOException {
        // Set prompt to global environment
        this.commandPrompt = prompt;
        this.commandInput = "";
        this.commandError = errorMessage;

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

    private String getStringWithValidation(String prompt, String errorMessage, String regex) throws IOException {
        String value;
        String currentErrorMessage = "";
        while (true) {
            value = getCommandWithRenderDisplay(prompt, currentErrorMessage);
            value = value.trim();
            if (!value.matches(regex)) {
                currentErrorMessage = errorMessage;
            } else {
                break;
            }
        }
        return value;
    }

    private int getIntegerWithValidation(String prompt, String errorMessage) throws IOException {
        String value;
        String currentErrorMessage = "";
        while (true) {
            value = getCommandWithRenderDisplay("Name: ", currentErrorMessage);
            value = value.trim();
            if (!value.matches("^\\d+$")) {
                currentErrorMessage = errorMessage;
            } else {
                break;
            }
        }
        return Integer.parseInt(value);
    }

    // returns time in milliseconds, specific implementation
    private long getTimeWithValidation() throws IOException {
        // matches the seconds: (?<=^(\d+\s*m\s*)?)\d+(?=\s*s?$)
        // matches the minutes: ^\d+(?=\s*m(\s*\d+\s*s?)?$)
        String value;
        String errorMessage = "";
        while (true) {
            value = getCommandWithRenderDisplay(
                    "Enter time with X seconds, Y minutes \n(ex. X, Xs, YmXs, YmX): ",
                    errorMessage);
            value = value.trim();

            Pattern secondPattern = Pattern.compile("(?<=^(\\d+\\s*m\\s*)?)\\d+(?=\\s*s?$)");
            Pattern minutePattern = Pattern.compile("^\\d+(?=\\s*m(\\s*\\d+\\s*s?)?$)");
            Matcher secondMatcher = secondPattern.matcher(value);
            Matcher minuteMatcher = minutePattern.matcher(value);

            if (!secondMatcher.find(0) && !minuteMatcher.find(0)) {
                errorMessage = "Pattern does not match one of: X, Xs, YmXs, YmX (for X secs, Y mins)";
            } else {
                long seconds = secondMatcher.find(0) ? Long.parseLong(secondMatcher.group()) : 0;
                long minutes = minuteMatcher.find(0) ? Long.parseLong(minuteMatcher.group()) : 0;
                return ((minutes * 60) + seconds) * 1000;
            }
        }
    }

    // Private render methods
    // --------------------------------------------------------------------------------------------

    // renders the terminal graphics on the screen and manages screen size changes
    private void renderScreen() throws IOException {
        screen.setCursorPosition(TerminalPosition.TOP_LEFT_CORNER);
        screen.clear();

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
    // error message must be one line
    private void renderCommandPromptAndInput(Screen screen) {
        if (commandPrompt != null) {
            TerminalPosition position = screen.getCursorPosition().withRelativeRow(1);

            // Print error message in red
            if (commandError != null && !commandError.isEmpty()) {
                TextGraphics errorDraw = screen.newTextGraphics();
                errorDraw.setForegroundColor(new TextColor.RGB(255, 0, 0));
                errorDraw.putString(position, commandError);
                position = position.withRelativeRow(1);
            }

            TextGraphics regularDraw = screen.newTextGraphics();

            // Get length of the last line in the command prompt, and total number of lines
            List<String> splitCommandPrompt = List.of(commandPrompt.split("\n"));
            int numLines = splitCommandPrompt.size();
            int lengthLastLine = splitCommandPrompt.get(splitCommandPrompt.size() - 1).length();

            // Output whole command prompt on multiple lines
            for (int i = 0; i < numLines; i++) {
                TerminalPosition linePosition = position.withRelativeRow(i);
                regularDraw.putString(linePosition, splitCommandPrompt.get(i));
            }

            // Output the user input, and set the cursor appropriately
            TerminalPosition commandPosition = position.withRelative(lengthLastLine, numLines - 1);
            regularDraw.putString(commandPosition, commandInput);
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

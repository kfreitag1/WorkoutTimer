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
import model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TerminalWorkoutTimerApp {
    private static final int TICKS_PER_SECOND = 30;
    private static final TextColor COLOUR_ERROR = new TextColor.RGB(237, 64, 78);
    private static final TextColor COLOUR_COMPLETE = new TextColor.RGB(143, 242, 107);
    private static final TextColor COLOUR_ACTIVE = new TextColor.RGB(50, 200, 235);


    private Screen screen;
    private TerminalSize terminalSize;

    private String applicationState; // One of: "main_menu", "routine", "running"
    private Routine activeRoutine; // null during the main_menu, set during routine

    // NOT FOR USE BY ANY FUNCTION EXCEPT getCommandWithRenderDisplay, renderCommandPromptAndInput
    private String commandPrompt; // can have newline characters, null unless getting input from user
    private String commandError; // has the error message of the previous input, invalid unless in getCommand function
    private String commandInput; // has the input of the user, invalid unless in getCommand function

    // --------------------------------------------------------------------------------------------
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

    // --------------------------------------------------------------------------------------------
    // Private application methods
    // --------------------------------------------------------------------------------------------

    // EFFECTS:
    private void applicationLoop() throws IOException, InterruptedException {
        long estimatedMilliseconds = 1000L / TICKS_PER_SECOND;

        long prevTime = 0;
        long diffTime = estimatedMilliseconds;

        while (tick(diffTime)) {
            diffTime = System.currentTimeMillis() - prevTime;
            prevTime = System.currentTimeMillis();
            Thread.sleep(estimatedMilliseconds);
        }

        System.exit(0);
    }

    // advance application by certain number of millis, returns false when the user quits the
    // application
    private boolean tick(long milliseconds) throws IOException {
        boolean keepGoing = handleUserInput();

        long prevTime = System.currentTimeMillis();
        if (applicationState.equals("running")) {
            long currentTime = System.currentTimeMillis();
            activeRoutine.advance(milliseconds);
        }
        renderScreen();

        return keepGoing;
    }

    // --------------------------------------------------------------------------------------------
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
                activeRoutine = makeRoutineFromInput();
                applicationState = "routine";
                break;
            case 'l': // load
                break;
            case 'q': // quit
                return false;
        }
        return true;
    }

    private Routine makeRoutineFromInput() throws IOException {
        String name = getStringWithValidation(
                "Routine name: ",
                "Name cannot be empty",
                "\\S+");

        return new Routine(name);
    }

    // handles input for the routine application state
    private boolean handleUserInputRoutine(KeyStroke stroke) throws IOException {
        if (!stroke.getKeyType().equals(KeyType.Character)) {
            return true;
        }

        switch (stroke.getCharacter()) {
            case 'p': // play
                // TODO: ensure isComplete = false before going to
                if (!activeRoutine.getSegments().isEmpty()) {
                    applicationState = "running";
                }
                break;
            case 'r': // restart
                activeRoutine.reset();
                break;
            case 'l': // load
                // TODO
                break;
            case 'a': // add
                activeRoutine.addSegment(makeSegmentFromInput());
                break;
            case 'd': // delete
                // TODO
                break;
            case 'e': // edit
                // TODO
                break;
            case 'c': // close
                // TODO
                break;
            case 's': // save
                // TODO
                break;
            case 'q': // quit
                return false;
        }
        return true;
    }

    private Segment makeSegmentFromInput() throws IOException {
        String name = getStringWithValidation(
                "Segment name: ", "Name cannot be empty", "\\S+");

        String type = getStringWithValidation(
                "Segment type: (t)imed, (r)epeat, (m)anual ",
                "Type is not one of 't', 'r', or 'm'", "[trm]");

        switch (type) {
            case "t": // make TimeSegment
                long milliseconds = getTimeWithValidation();
                return new TimeSegment(name, milliseconds);
            case "r":
                int numRepeats = getIntegerWithValidation(
                        "Number of repetitions (1-99): ",
                        "Invalid input (not an integer between 1 and 99)", "^[1-9][0-9]?$");

                List<Segment> children = new ArrayList<>();
                do { // adds at least one child
                    children.add(makeSegmentFromInput());
                } while (getBooleanWithValidation("Add another segment? (y/n) "));

                return new RepeatSegment(name, numRepeats, children);
            case "m":
                return new ManualSegment(name);
            default:
                return new ManualSegment("INVALID"); // Will never reach here
        }
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

    // --------------------------------------------------------------------------------------------
    // Private user input helper methods
    // --------------------------------------------------------------------------------------------

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

    // same above but with generic regex condition
    private int getIntegerWithValidation(String prompt, String errorMessage) throws IOException {
        return getIntegerWithValidation(prompt, errorMessage, "^\\d+$");
    }

    private int getIntegerWithValidation(String prompt, String errorMessage, String regex) throws IOException {
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

    private boolean getBooleanWithValidation(String prompt) throws IOException {
        String value;
        String errorMessage = "";
        while (true) {
            value = getCommandWithRenderDisplay(prompt, errorMessage);
            value = value.trim().toLowerCase();
            if (!value.matches("[yn]")) {
                errorMessage = "Invalid input (not y or n)";
            } else {
                break;
            }
        }
        return value.equals("y");
    }

    // --------------------------------------------------------------------------------------------
    // Private render methods
    // --------------------------------------------------------------------------------------------

    // renders the terminal graphics on the screen and manages screen size changes
    private void renderScreen() throws IOException {
        screen.setCursorPosition(TerminalPosition.TOP_LEFT_CORNER);
        screen.clear();

        // Adjust terminal size if necessary
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
    // error message must be one line
    private void renderCommandPromptAndInput(Screen screen) {
        if (commandPrompt != null) {
            TerminalPosition position = screen.getCursorPosition().withRelativeRow(1);

            // Print error message in red
            if (commandError != null && !commandError.isEmpty()) {
                TextGraphics errorDraw = screen.newTextGraphics();
                errorDraw.setForegroundColor(COLOUR_ERROR);
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
        draw.putString(screen.getCursorPosition(), "press command: (n)ew (l)oad (q)uit");
        advanceCursorOneRow(screen);
    }

    // requires that activeRoutine is not null
    // handles input for the routine application state
    // modifies cursor position to be after whatever this is
    private void renderRoutine(Screen screen) {
        TerminalPosition position = screen.getCursorPosition();
        TextGraphics draw = screen.newTextGraphics();
        draw.putString(position, "press command: (c)lose (s)ave (p)lay (r)estart (a)dd (d)elete (e)dit");
        draw.putString(position.withRelativeRow(1), "Title: " + activeRoutine.getName());
        advanceCursorBy(screen, 3);

        List<Segment> topLayerSegments = activeRoutine.getSegments();
        if (topLayerSegments.isEmpty()) {
            draw.putString(screen.getCursorPosition(), "no segments yet!");
            advanceCursorOneRow(screen);
        } else {
            renderRoutineLayer(screen, activeRoutine.getSegments(), false);
        }
    }

    private void renderRoutineLayer(Screen screen, List<Segment> layerSegments, boolean isActive) {
        renderRoutineLayer(screen, layerSegments, isActive, 0);
    }

    // modifies cursor position to be after whatever this is
    private void renderRoutineLayer(Screen screen, List<Segment> layerSegments, boolean isActive, int layer) {
        Segment currentSegment = null;
        if (!activeRoutine.isComplete()) {
            currentSegment = activeRoutine.getExactCurrentSegment();
        }

        for (int i = 0; i < layerSegments.size(); i++) {
            Segment segment = layerSegments.get(i);
            TextGraphics draw = screen.newTextGraphics();
            TerminalPosition position = screen.getCursorPosition().withRelativeColumn(layer * 2);

            // Set color depending on completion state and if currently active
            if (isActive && segment.isComplete()) {
                draw.setForegroundColor(COLOUR_COMPLETE);
            } else if (isActive && segment.equals(currentSegment)) {
                draw.setForegroundColor(COLOUR_ACTIVE);
            }

            switch (segment.getType()) {
                case "time":
                    TimeSegment timeSegment = (TimeSegment) segment;
                    draw.putString(position,
                            "TIME " + timeSegment.getName() + " "
                                    + timeSegment.getCurrentTime() + "/"
                                    + timeSegment.getTotalTime());
                    advanceCursorOneRow(screen);
                    break;
                case "manual":
                    draw.putString(position, "MANUAL " + segment.getName()
                            + (isActive && segment.equals(currentSegment) ? " Press enter to continue!" : ""));
                    advanceCursorOneRow(screen);
                    break;
                case "repeat":
                    RepeatSegment repeatSegment = (RepeatSegment) segment;
                    draw.putString(position,
                            "REPEAT " + repeatSegment.getName() + " "
                                    + repeatSegment.getCurrentRepetition() + "/"
                                    + repeatSegment.getTotalRepetitions());
                    advanceCursorOneRow(screen);

                    renderRoutineLayer(screen, repeatSegment.getSegments(), isActive, layer + 1);
            }
        }
    }

    private void advanceCursorOneRow(Screen screen) {
        advanceCursorBy(screen, 1);
    }

    // requires count > 0
    private void advanceCursorBy(Screen screen, int count) {
        screen.setCursorPosition(screen.getCursorPosition().withRelativeRow(count));
    }

    // handles input for the running application state
    // modifies cursor position to be after whatever this is
    private void renderRunning(Screen screen) {
        TerminalPosition position = screen.getCursorPosition();
        TextGraphics draw = screen.newTextGraphics();
        draw.putString(position, "press command: (p)ause");
        draw.putString(position.withRelativeRow(1), "Title: " + activeRoutine.getName());
        advanceCursorBy(screen, 3);

        renderRoutineLayer(screen, activeRoutine.getSegments(), true);
    }
}

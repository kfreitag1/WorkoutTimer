package ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Represents a terminal-based app for WorkoutTimer
public class TerminalWorkoutTimerApp {
    private static final int ESTIMATED_TICKS_PER_SECOND = 30;
    private static final TextColor COLOUR_ERROR = new TextColor.RGB(237, 64, 78);
    private static final TextColor COLOUR_COMPLETE = new TextColor.RGB(143, 242, 107);
    private static final TextColor COLOUR_ACTIVE = new TextColor.RGB(50, 200, 235);

    private Screen screen;
    private TerminalSize terminalSize;

    private String applicationState; // One of: "main_menu", "routine", "running"
    private Routine activeRoutine; // null during the main_menu, set during routine and running states
    private boolean displaySegmentIndices = false;

    // NOT FOR USE BY ANY FUNCTION EXCEPT getCommandWithRenderDisplay, renderCommandPromptAndInput
    private String commandPrompt; // Can have newline characters, ALWAYS null unless actively getting input from user
    private String commandError; // Has the error message of the prev. input, INDETERMINATE unless getting input
    private String commandInput; // Has the current input, INDETERMINATE unless getting input

    // --------------------------------------------------------------------------------------------
    // Public methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Starts the application by initializing all values and the application state
    //          then begins the application loop
    public void start() throws IOException, InterruptedException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        screen = terminalFactory.createScreen();
        screen.startScreen();

        changeApplicationState("main_menu");

        // Initiate application input/output loop
        applicationLoop();
    }

    // --------------------------------------------------------------------------------------------
    // Private application methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Contains the loop that is run for each 'tick' of the application execution,
    //          keeps track of the milliseconds between subsequent ticks to provide accurate
    //          timing information for the model. Exits the application when the inner tick
    //          function returns false.
    private void applicationLoop() throws IOException, InterruptedException {
        long estimatedMilliseconds = 1000L / ESTIMATED_TICKS_PER_SECOND;

        long prevTime = 0;
        long diffTime = estimatedMilliseconds;

        while (tick(diffTime)) {
            diffTime = System.currentTimeMillis() - prevTime;
            prevTime = System.currentTimeMillis();
            Thread.sleep(estimatedMilliseconds);
        }

        System.exit(0);
    }

    // MODIFIES: this
    // EFFECTS: Runs for every tick in the application execution, given the number of milliseconds
    //          to advance by. Handles user input, model updating, and screen rendering. Returns
    //          false when the user requests to exit the application.
    private boolean tick(long milliseconds) throws IOException {
        boolean keepGoing = handleUserInput();

        if (applicationState.equals("running")) {
            activeRoutine.advance(milliseconds);
        }
        renderScreen();

        return keepGoing;
    }

    // REQUIRES: newState is one of "main_menu", "routine", "running"
    // MODIFIES: this
    // EFFECTS: Changes the application state to the given new state. Ensures that
    //          activeRoutine is consistent with this requested change. Returns true
    //          if the application state is successfully changed, false otherwise.
    private boolean changeApplicationState(String newState) {
        switch (newState) {
            case "main_menu":
                activeRoutine = null;
                applicationState = "main_menu";
                break;
            case "routine":
                if (activeRoutine != null) {
                    applicationState = "routine";
                    return true;
                } else {
                    return false;
                }
            case "running":
                if (activeRoutine != null && !activeRoutine.getSegments().isEmpty()) {
                    applicationState = "running";
                    return true;
                } else {
                    return false;
                }
        }
        return false; // Will never reach here
    }

    // --------------------------------------------------------------------------------------------
    // Private user input methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Handles user input by polling for any key presses and then redirecting
    //          the information to the corresponding handler depending on the current application
    //          state. Returns false if the user requests to quit the application, true otherwise.
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

    // MODIFIES: this
    // EFFECTS: Handles user input in the case where the application state is at the main menu.
    //          Runs any corresponding methods depending on the command requested. Available commands
    //          are one of: n (create new routine), l (load existing routine), q (quit application)
    private boolean handleUserInputMainMenu(KeyStroke stroke) throws IOException {
        if (!stroke.getKeyType().equals(KeyType.Character)) {
            return true;
        }

        switch (stroke.getCharacter()) {
            case 'n': // new
                activeRoutine = makeRoutineFromInput();
                changeApplicationState("routine");
                break;
            case 'l': // load
                // TODO
                break;
            case 'q': // quit
                // TODO
                return false;
        }
        return true;
    }

    // MODIFIES: this (only temporary getCommandWithRenderDisplay variables though)
    // EFFECTS: Procedure to make a new routine based on user input. Returns the constructed routine.
    private Routine makeRoutineFromInput() throws IOException {
        String name = getStringWithValidation(
                "Routine name: ",
                "Name cannot be empty",
                ".+"); // matches any non-empty string because trims

        return new Routine(name);
    }

    // MODIFIES: this
    // EFFECTS: Handles user input in the case where the application state is at the routine menu.
    //          Runs any corresponding methods depending on the command requested. Available commands
    //          are one of: p (play current routine), r (restart current routine), a (add new segment to
    //          end of current routine), d (delete segment from current routine), e (edit segment from
    //          current routine), i (insert segment into current routine), c (close current routine),
    //          s (save current routine)
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private boolean handleUserInputRoutine(KeyStroke stroke) throws IOException {
        if (!stroke.getKeyType().equals(KeyType.Character)) {
            return true;
        }

        switch (stroke.getCharacter()) {
            case 'p': // play
                changeApplicationState("running");
                break;
            case 'r': // restart
                activeRoutine.reset();
                break;
            case 'a': // add
                activeRoutine.addSegment(makeSegmentFromInput());
                break;
            case 'd': // delete
                deleteSegment();
                break;
            case 'e': // edit
                editSegment();
                break;
            case 'i': // insert
                insertSegment();
                break;
            case 'c': // close
                // TODO : ask user to save before closing
                changeApplicationState("main_menu");
                break;
            case 's': // save
                // TODO
                break;
        }
        return true;
    }

    // MODIFIES: this (only temporary getCommandWithRenderDisplay variables though)
    // EFFECTS: Procedure to make a new segment based on user input. Returns the constructed segment.
    private Segment makeSegmentFromInput() throws IOException {
        String name = getStringWithValidation(
                "Segment name: ", "Name cannot be empty",
                ".+"); // Matches any non-empty string since whitespace is trimmed

        String type = getStringWithValidation(
                "Segment type: (t)imed, (r)epeat, (m)anual ",
                "Type is not one of 't', 'r', or 'm'", "[trm]");

        switch (type) {
            case "t":
                long milliseconds = getTimeWithValidation();
                return new TimeSegment(name, milliseconds);
            case "r":
                int numRepeats = getIntegerWithValidation(
                        "Number of repetitions (1 - 1000): ", 1, 1000);

                List<Segment> children = new ArrayList<>();
                do { // adds at least one child
                    children.add(makeSegmentFromInput());
                } while (getBooleanWithValidation("Add another segment? (y/n) "));

                return new RepeatSegment(name, numRepeats, children);
            case "m":
                return new ManualSegment(name);
        }
        return null; // Will never reach here
    }

    // MODIFIES: this
    // EFFECTS: Procedure to delete a segment based on user input. Removes the segment from
    //          the active routine.
    private void deleteSegment() throws IOException {
        if (activeRoutine.getSegments().isEmpty()) {
            return;
        }
        Segment segmentToDelete = getSegmentIndexAtInput("Enter index of segment to delete: ");
        activeRoutine.removeSegment(segmentToDelete);
    }

    // MODIFIES: this
    // EFFECTS: Procedure to edit a segment based on user input. Edits the segment in the current routine
    //          based on its type.
    private void editSegment() throws IOException {
        if (activeRoutine.getSegments().isEmpty()) {
            return;
        }

        Segment segmentToEdit = getSegmentIndexAtInput("Enter index of segment to edit: ");
        segmentToEdit.setName(getStringWithValidation(
                "New segment name: ", "Name cannot be empty",
                ".+", segmentToEdit.getName()));

        switch (segmentToEdit.getType()) {
            case "time":
                TimeSegment timeSegment = (TimeSegment) segmentToEdit;
                timeSegment.setTotalTime(getTimeWithValidation());
                break;
            case "repeat":
                RepeatSegment repeatSegment = (RepeatSegment) segmentToEdit;
                repeatSegment.setNewRepeats(getIntegerWithValidation("New number of repetitions (1 to 1000): ",
                        1, 1000, Integer.toString(repeatSegment.getTotalRepetitions())));
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: Procedure to insert a new segment based on user input. Constructs the new segment and
    //          inserts it before or after the specified location in the current routine.
    private void insertSegment() throws IOException {
        if (activeRoutine.getSegments().isEmpty()) {
            return;
        }

        String beforeOrAfter = getStringWithValidation(
                "Insert (b)efore or (a)fter: ",
                "Invalid input, not one of 'a' or 'b'",
                "[ab]");
        Segment segmentToInsertAround = getSegmentIndexAtInput(
                "Enter index to insert " + (beforeOrAfter.equals("a") ? "after" : "before") + ": ");
        Segment newSegment = makeSegmentFromInput();

        if (beforeOrAfter.equals("a")) {
            activeRoutine.insertSegmentAfter(newSegment, segmentToInsertAround);
        } else {
            activeRoutine.insertSegmentBefore(newSegment, segmentToInsertAround);
        }
    }

    // REQUIRES: Current application state is "routine" (activeRoutine is not null),
    //           activeRoutine.getSegments() is not empty
    // MODIFIES: this
    // EFFECTS: Returns the segment at the index specified by the user. Re-renders the screen
    //          with indices beside each segment in the routine during the function, returns to normal
    //          before the function is complete.
    private Segment getSegmentIndexAtInput(String prompt) throws IOException {
        // Make the render function add in indices beside each index
        displaySegmentIndices = true;

        List<Segment> flattenedSegments = activeRoutine.getFlattenedSegments();
        int startIndex = 0;
        int endIndex = flattenedSegments.size() - 1;

        int segmentIndex = getIntegerWithValidation(
                prompt + "(" + startIndex + " to " + endIndex + "): ",
                startIndex, endIndex);

        // Stop displaying the segment indices
        displaySegmentIndices = false;

        return activeRoutine.getFlattenedSegments().get(segmentIndex);
    }

    // MODIFIES: this
    // EFFECTS: Handles user input in the case where the application state is running.
    //          Runs any corresponding methods depending on the command requested. Available commands
    //          are one of: p (pause current routine). ALSO try to advance the routine if the user has pressed
    //          SPACE (advances any currently active manual segments).
    private boolean handleUserInputRunning(KeyStroke stroke) {
        switch (stroke.getCharacter()) {
            case 'p':
                changeApplicationState("routine");
                break;
            case ' ':
                activeRoutine.advance();
                break;
        }
        return true;
    }

    // --------------------------------------------------------------------------------------------
    // Private user input helper methods
    // --------------------------------------------------------------------------------------------

    // Convenience method for getCommandWithRenderDisplay with no defaultInput
    private String getCommandWithRenderDisplay(String prompt,
                                               String errorMessage) throws IOException {
        return getCommandWithRenderDisplay(prompt, errorMessage, "");
    }

    // MODIFIES: this (only temporary variables though)
    // EFFECTS: Returns user input: a string of inputted characters, returns when the user presses
    //          ENTER. Displays the given prompt, errorMessage (if not empty string), and default input
    //          (initial state for the user input). Renders the screen on each button press so that the
    //          user can see this information and what they are typing. Uses global variables to share
    //          information about the prompt, input, and error message with the render function, BUT
    //          ensures that the global prompt variable is ALWAYS null when this function is complete.
    private String getCommandWithRenderDisplay(String prompt,
                                               String errorMessage,
                                               String defaultInput) throws IOException {
        // Set shared variables to global environment
        this.commandPrompt = prompt;
        this.commandInput = defaultInput;
        this.commandError = errorMessage;

        // Input is complete when the enter key is pressed
        while (true) {
            // Render the screen on each button press so that the user can see what they are typing
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

    // Convenience method for getStringWithValidation with no default input
    private String getStringWithValidation(String prompt,
                                           String errorMessage,
                                           String regex) throws IOException {
        return getStringWithValidation(prompt, errorMessage, regex, "");
    }

    // REQUIRES: regex is a valid regular expression
    // MODIFIES: this (only temporary getCommandWithRenderDisplay variables though)
    // EFFECTS: Gets a string from the user, ensuring that it matches to the given regular expression.
    //          If the user input is invalid, displays the provided error message and tries again. Keeps
    //          looping until a valid string is inputted.
    private String getStringWithValidation(String prompt,
                                           String errorMessage,
                                           String regex,
                                           String defaultInput) throws IOException {
        String value;
        String currentErrorMessage = "";
        while (true) {
            value = getCommandWithRenderDisplay(prompt, currentErrorMessage, defaultInput);
            value = value.trim();
            if (!value.matches(regex)) {
                currentErrorMessage = errorMessage;
            } else {
                break;
            }
        }
        return value;
    }

    // Convenience method for getIntegerWithValidation with no defaultInput
    private int getIntegerWithValidation(String prompt,
                                         int min, int max) throws IOException {
        return getIntegerWithValidation(prompt, min, max, "");
    }

    // REQUIRES: min <= max
    // MODIFIES: this (only temporary getCommandWithRenderDisplay variables though)
    // EFFECTS: Gets an integer from the user, ensuring that it is within the specified range.
    //          If the user input is invalid, displays an invalid range error message and tries again. Keeps
    //          looping until a valid integer is inputted.
    private int getIntegerWithValidation(String prompt,
                                         int min, int max,
                                         String defaultInput) throws IOException {
        String value;
        String currentErrorMessage = "";
        while (true) {
            value = getCommandWithRenderDisplay(prompt, currentErrorMessage, defaultInput);
            value = value.trim();
            if (!value.matches("^\\d+$")) {
                currentErrorMessage = "Value is not in the range from " + min + " to " + max;
            } else if (Integer.parseInt(value) < min || Integer.parseInt(value) > max) {
                currentErrorMessage = "Value is not in the range from " + min + " to " + max;
            } else {
                break;
            }
        }
        return Integer.parseInt(value);
    }

    // MODIFIES: this (only temporary getCommandWithRenderDisplay variables though)
    // EFFECTS: Gets time (in milliseconds) from the user. If the user input is invalid, displays an error message
    //          and tries again. Keeps looping until a valid time is inputted.
    private long getTimeWithValidation() throws IOException {
        String value;
        String errorMessage = "";
        while (true) {
            value = getCommandWithRenderDisplay(
                    "Enter time with X seconds, Y minutes \n(ex. X, Xs, YmXs, YmX): ",
                    errorMessage);
            value = value.trim().toLowerCase();

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

    // MODIFIES: this (only temporary getCommandWithRenderDisplay variables though)
    // EFFECTS: Gets a boolean value from the user. If the user input is invalid, displays an error message
    //          and tries again. Keeps looping until a valid boolean value is inputted.
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

    // MODIFIES: this
    // EFFECTS: Renders the application onto the global screen variable, using the appropriate corresponding
    //          render methods for each application state. Adjusts the global terminalSize variable if the
    //          screen size changes.
    private void renderScreen() throws IOException {
        screen.setCursorPosition(TerminalPosition.TOP_LEFT_CORNER);
        screen.clear();

        // Adjust terminal size if necessary
        if (screen.doResizeIfNecessary() != null) {
            terminalSize = screen.getTerminalSize();
        }

        // Render state specific stuff
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

        // Render the command prompt and input if necessary
        renderCommandPromptAndInput(screen);

        // Display the contents of the screen onto the terminal
        screen.refresh();
    }

    // MODIFIES: this, screen
    // EFFECTS: Renders the command prompt, error message, and input onto the given screen IF the global
    //          commandPrompt  is NOT null (only non-null when the getCommandWithRenderDisplay function is actively
    //          running). Ensures that the screen's cursor position is appropriately placed on the line after
    //          all the drawing operations.
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

    // REQUIRES: Current application state is "main_menu"
    // MODIFIES: screen
    // EFFECTS: Renders the main menu screen onto the given screen. Ensures that the cursor position of
    //          the screen is after whatever is displayed by this function.
    private void renderMainMenu(Screen screen) {
        TextGraphics draw = screen.newTextGraphics();
        // TODO: final list of commands: (n)ew (l)oad (q)uit
        draw.putString(screen.getCursorPosition(), "Press command: (n)ew (q)uit");
        advanceCursorOneRow(screen);
    }

    // REQUIRES: Current application state is "routine" (i.e. activeRoutine is not null)
    // MODIFIES: screen
    // EFFECTS: Renders the routine screen onto the given screen. Ensures that the cursor position of
    //          the screen is after whatever is displayed by this function.
    private void renderRoutine(Screen screen) {
        TerminalPosition position = screen.getCursorPosition();
        TextGraphics draw = screen.newTextGraphics();
        // TODO: final list of commands: (c)lose (s)ave (p)lay (r)estart (a)dd (i)nsert (d)elete (e)dit
        draw.putString(position, "Press command: (c)lose (p)lay (r)estart (a)dd (i)nsert (d)elete (e)dit");
        draw.putString(position.withRelativeRow(1), "Title: " + activeRoutine.getName());
        advanceCursorBy(screen, 3);

        List<Segment> topLayerSegments = activeRoutine.getSegments();
        if (topLayerSegments.isEmpty()) {
            draw.putString(screen.getCursorPosition(), "no segments yet!");
            advanceCursorOneRow(screen);
        } else {
            renderSegmentLayer(screen, activeRoutine.getSegments(), false, 0);
        }
    }

    // REQUIRES: Current application state is "running" (i.e. activeRoutine is not null)
    // MODIFIES: screen
    // EFFECTS: Renders the routine screen onto the given screen. Ensures that the cursor position of
    //          the screen is after whatever is displayed by this function.
    private void renderRunning(Screen screen) {
        TerminalPosition position = screen.getCursorPosition();
        TextGraphics draw = screen.newTextGraphics();
        draw.putString(position, "Press command: (p)ause");
        draw.putString(position.withRelativeRow(1), "Title: " + activeRoutine.getName());
        advanceCursorBy(screen, 3);

        renderSegmentLayer(screen, activeRoutine.getSegments(), true, 0);
    }

    // REQUIRES: Current application state is "routine" or "running" (i.e. activeRoutine is not null)
    // MODIFIES: screen
    // EFFECTS: Renders the given list of segments (at a given layer) onto the given screen. Recursively calls
    //          this function for the child segments within any RepeatSegment objects in layerSegments. If
    //          isActive is true then display the segments in an active state (i.e. segments are coloured based
    //          on their completion or currently active status). Ensures that the cursor position of
    //          the screen is after whatever is displayed by this function.
    private void renderSegmentLayer(Screen screen, List<Segment> layerSegments, boolean isActive, int layer) {
        // Get the current active segment if valid, otherwise keep this as null
        Segment activeSegment = null;
        if (!activeRoutine.isComplete()) {
            activeSegment = activeRoutine.getExactCurrentSegment();
        }

        for (Segment segment : layerSegments) {
            // Set color depending on completion state and if currently active
            TextGraphics draw = screen.newTextGraphics();
            if (isActive && segment.isComplete()) {
                draw.setForegroundColor(COLOUR_COMPLETE);
            } else if (isActive && segment.equals(activeSegment)) {
                draw.setForegroundColor(COLOUR_ACTIVE);
            }

            // Build line to display segment, include:
            // 1. line number (optional), 2. spacing for different layers, 3. segment name,
            // 4. segment type specific data
            String segmentDisplay = displaySegmentIndices
                    ? activeRoutine.getFlattenedSegments().indexOf(segment) + "\t" : "";
            segmentDisplay += "   ".repeat(layer) + segment.getName() + " ";
            segmentDisplay += displayTextForSegment(segment, isActive, activeSegment);
            draw.putString(screen.getCursorPosition(), segmentDisplay);
            advanceCursorOneRow(screen);

            // Iteratively go through all children in any RepeatSegments, children are on a different layer
            if (segment.getType().equals("repeat")) {
                renderSegmentLayer(screen, ((RepeatSegment) segment).getSegments(), isActive, layer + 1);
            }
        }
    }

    // EFFECTS: Returns a string that represents the given segment. Can have different string representations
    //          based on whether the routine is currently running (isActive), and what the current active
    //          segment is (could be null if no valid active segment).
    private String displayTextForSegment(Segment segment, boolean isActive, Segment activeSegment) {
        switch (segment.getType()) {
            case "time":
                TimeSegment timeSegment = (TimeSegment) segment;
                String currentTimeString = millisecondsToPrettyTime(timeSegment.getCurrentTime(), true);
                String totalTimeString = millisecondsToPrettyTime(timeSegment.getTotalTime(), false);
                return currentTimeString + " / " + totalTimeString;
            case "manual":
                return isActive && segment.equals(activeSegment) ? "Press enter to continue!" : "";
            case "repeat":
                RepeatSegment repeatSegment = (RepeatSegment) segment;
                return repeatSegment.getCurrentRepetition() + "/" + repeatSegment.getTotalRepetitions();
        }
        return null; // Will never reach here
    }

    // --------------------------------------------------------------------------------------------
    // Private utility methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: screen
    // EFFECTS: Advances the cursor on the provided screen down by one row.
    private void advanceCursorOneRow(Screen screen) {
        advanceCursorBy(screen, 1);
    }

    // REQUIRES: count > 0
    // MODIFIES: screen
    // EFFECTS: Advances the cursor on the provided screen down by the specified number of rows.
    private void advanceCursorBy(Screen screen, int count) {
        screen.setCursorPosition(screen.getCursorPosition().withRelativeRow(count));
    }

    // REQUIRES: milliseconds >= 0
    // EFFECTS: Returns a fancy string representation of the given time (in milliseconds). The string
    //          representation will be of the form X:YY if there's at least one minute, Y if there is less
    //          than 1 minute, and will have .Z after it if includeDecimalOutput is true (for X minutes,
    //          Y seconds, Z deciseconds in the given time).
    private String millisecondsToPrettyTime(long milliseconds, boolean includeDecimalOutput) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        long deciseconds = (milliseconds / 100) % 10;

        String output = "";
        if (minutes > 0) {
            output += minutes + ":";
            output += String.format("%02d", seconds);
        } else {
            output += Long.toString(seconds);
        }
        output += includeDecimalOutput ? "." + deciseconds : "";
        return output;
    }
}

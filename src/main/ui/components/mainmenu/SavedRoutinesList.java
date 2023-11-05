package ui.components.mainmenu;

import model.Routine;
import persistence.RoutineReader;
import ui.components.ScrollableComponent;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Represents a list of routines as read from the filesystem
public class SavedRoutinesList extends ScrollableComponent {
    private final RoutineReceiver receiver;

    // EFFECTS: Construct and display a new routine list from saved routines
    public SavedRoutinesList(RoutineReceiver receiver) {
        super();
        this.receiver = receiver;

        body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));

        render();
    }

    // MODIFIES: this
    // EFFECTS: Clears the display and renders a list of RoutineListItems using
    //          information from saved routines in the filesystem
    private void render() {
        body.removeAll();

        // Pattern to only detect json files
        Pattern jsonPattern = Pattern.compile(".+(?=\\.json$)");

        // Pre-determined directory to store all saved routines
        Path savedRoutinesPathname = Paths.get("data", "savedroutines");

        // Loop through all files in the saved routines directory
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(savedRoutinesPathname)) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    String filename = path.getFileName().toString();
                    Matcher jsonMatcher = jsonPattern.matcher(filename);

                    // Check if the file is a .json file, then assume it must be a routine!
                    if (jsonMatcher.find(0)) {
                        body.add(new RoutineListItem(jsonMatcher.group(), filename));
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error in loading files!");
        }

        repaint();
        revalidate();
    }

    // Represents a single item in a saved routine list
    private class RoutineListItem extends JComponent {

        // EFFECTS: Constructs a new routine list item with the given routine name and
        //          filename (in the /data/savedroutines directory).
        public RoutineListItem(String name, String filename) {
            super();
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

            // Make elements
            ClickableLink routineName = makeRoutineLink(name, filename);
            JButton deleteButton = makeDeleteButton(name, filename);

            // Layout elements
            add(routineName);
            add(Box.createHorizontalGlue());
            add(deleteButton);
        }

        // EFFECTS: Constructs a new clickable routine link that will open the saved routine.
        private ClickableLink makeRoutineLink(String name, String filename) {
            return new ClickableLink(name, () -> {
                String filepath = Paths.get("data", "savedroutines", filename).toString();
                RoutineReader routineReader = new RoutineReader(filepath);

                // Read routine at the filepath and pass it to the callback to open routine
                try {
                    Routine routine = routineReader.read();
                    receiver.receiveRoutine(routine);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error in reading routine file!");
                }
            });
        }

        // EFFECTS: Constructs a delete button to initiate the procedure to delete the saved
        //          routine at the given filename. First confirms with the user that they really
        //          want to delete the routine before actually deleting it.
        private JButton makeDeleteButton(String name, String filename) {
            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete " + name + "?",
                        "Delete Routine",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {
                    try {
                        Path pathname = Paths.get("data", "savedroutines", filename);
                        Files.delete(pathname);
                        render();
                    } catch (IOException e2) {
                        JOptionPane.showMessageDialog(null, "Error in deleting file!");
                    }
                }
            });
            return deleteButton;
        }
    }
}

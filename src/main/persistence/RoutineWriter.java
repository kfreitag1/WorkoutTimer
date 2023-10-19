package persistence;

import model.Routine;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

// Represents a writer that writes constructs files to store encoded (JSON) routines
// Based directly from the examples given in JSONSerializationDemo
public class RoutineWriter {
    private static final int TAB = 4;
    private PrintWriter writer;
    private final String destinationFilepath;

    // EFFECTS: constructs writer to write to destination file
    public RoutineWriter(String destinationFilepath) {
        this.destinationFilepath = destinationFilepath;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if destination file cannot
    // be opened for writing
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(destinationFilepath);
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of workroom to file
    public void write(Routine routine) {
        JSONObject json = routine.encoded();
        saveToFile(json.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }
}

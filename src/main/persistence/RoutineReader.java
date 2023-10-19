package persistence;

import model.*;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// Represents a reader that reads files to extract encoded (JSON) routines
// Based directly from the examples given in JSONSerializationDemo
public class RoutineReader {
    private final String sourceFilepath;

    // EFFECTS: Constructs reader to read from source filepath (relative)
    public RoutineReader(String sourceFilepath) {
        this.sourceFilepath = sourceFilepath;
    }

    // EFFECTS: Reads routine from file and returns it;
    //          throws IOException if an error occurs reading data from file
    //          (i.e. does not conform to expected structure)
    public Routine read() throws IOException {
        String jsonData = readFile(sourceFilepath);
        JSONObject jsonObject = new JSONObject(jsonData);

        String name = jsonObject.getString("name");
        Routine routine = new Routine(name);

        for (Object object : jsonObject.getJSONArray("segments")) {
            Segment segment = readSegmentFromJson((JSONObject) object);
            routine.addSegment(segment);
        }

        return routine;
    }

    // EFFECTS: Reads and constructs a Segment from the provided JSON object
    //          throws IOException if an error occurs reading data from the object
    //          (i.e. does not conform to expected structure)
    private Segment readSegmentFromJson(JSONObject object) throws IOException {
        String name = object.getString("name");
        String type = object.getString("type");

        switch (type) {
            case "time":
                long totalTime = object.getLong("totalTime");
                long currentTime = object.getLong("currentTime");
                return new TimeSegment(name, totalTime, currentTime);
            case "manual":
                boolean finished = object.getBoolean("finished");
                return new ManualSegment(name, finished);
            case "repeat":
                int totalRepetitions = object.getInt("totalRepetitions");
                int currentRepetitions = object.getInt("currentRepetitions");
                List<Segment> children = new ArrayList<>();
                for (Object subObject : object.getJSONArray("children")) {
                    children.add(readSegmentFromJson((JSONObject) subObject));
                }
                return new RepeatSegment(name, totalRepetitions, children, currentRepetitions);
            default:
                throw new IOException();
        }
    }

    // EFFECTS: Reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }
}

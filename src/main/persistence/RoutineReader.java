package persistence;

import model.*;
import org.json.JSONException;
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
        try {
            String jsonData = readFile(sourceFilepath);
            JSONObject jsonObject = new JSONObject(jsonData);

            String name = jsonObject.getString(RoutineJsonKey.NAME.toString());
            Routine routine = new Routine(name);

            for (Object object : jsonObject.getJSONArray(RoutineJsonKey.SEGMENTS.toString())) {
                Segment segment = readSegmentFromJson((JSONObject) object);
                routine.addSegment(segment);
            }

            return routine;
        } catch (JSONException e) {
            // Catch JSONException and rethrow as IOException
            throw new IOException(e.getMessage());
        }
    }

    // EFFECTS: Reads and constructs a Segment from the provided JSON object
    //          throws IOException if an error occurs reading data from the object
    //          (i.e. does not conform to expected structure)
    private Segment readSegmentFromJson(JSONObject object) throws IOException {
        try {
            String name = object.getString(RoutineJsonKey.NAME.toString());
            String typeString = object.getString(RoutineJsonKey.TYPE.toString());
            SegmentType type = SegmentType.valueOf(typeString);

            if (type == SegmentType.TIME) {
                long totalTime = object.getLong(RoutineJsonKey.TOTAL_TIME.toString());
                long currentTime = object.getLong(RoutineJsonKey.CURRENT_TIME.toString());
                return new TimeSegment(name, totalTime, currentTime);
            }

            if (type == SegmentType.MANUAL) {
                boolean finished = object.getBoolean(RoutineJsonKey.FINISHED.toString());
                return new ManualSegment(name, finished);
            }

            // Must be SegmentType.REPEAT since type is never null (would throw otherwise)
            int totalRepetitions = object.getInt(RoutineJsonKey.TOTAL_REPETITIONS.toString());
            int currentRepetitions = object.getInt(RoutineJsonKey.CURRENT_REPETITIONS.toString());
            List<Segment> children = new ArrayList<>();
            for (Object subObject : object.getJSONArray(RoutineJsonKey.CHILDREN.toString())) {
                children.add(readSegmentFromJson((JSONObject) subObject));
            }
            return new RepeatSegment(name, totalRepetitions, children, currentRepetitions);
        } catch (Exception e) {
            // Catch JSONException and IllegalArgumentException and rethrow as IOException
            throw new IOException(e.getMessage());
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

package persistence;

public enum RoutineJsonKey {
    // Keys for main Routine object
    NAME("name"), // Also used for segments
    SEGMENTS("segments"),

    // Keys for Segment objects
    TYPE("type"),
    TOTAL_TIME("totalTime"),
    CURRENT_TIME("currentTime"),
    FINISHED("finished"),
    TOTAL_REPETITIONS("totalRepetitions"),
    CURRENT_REPETITIONS("currentRepetitions"),
    CHILDREN("children");

    private final String stringRepresentation;

    // EFFECTS: Private constructor with a string representation of the JSON key
    RoutineJsonKey(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    // EFFECTS: Gets the string representation of the JSON key,
    //          i.e. what to actually store in the JSON file
    @Override
    public String toString() {
        return stringRepresentation;
    }
}

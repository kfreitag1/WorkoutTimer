package model;

import org.json.JSONObject;
import persistence.RoutineJsonKey;

import static java.lang.Math.min;

// Represents a Segment that is completed after a certain amount of time
public class TimeSegment extends Segment {
    private long totalTime;
    private long currentTime;

    // --------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------

    // REQUIRES: totalTime >= 0
    // EFFECTS: Constructs a new time segment with the given name, total time (in milliseconds),
    //          and no current time elapsed.
    public TimeSegment(String name, long totalTime) {
        this(name, totalTime, 0);
    }

    // REQUIRES: totalTime >= 0
    // EFFECTS: Constructs a partially completed time segment with the given name, total time (in milliseconds),
    //          and the given time elapsed.
    public TimeSegment(String name, long totalTime, long currentTime) {
        super(name);
        this.totalTime = totalTime;
        this.currentTime = currentTime;
    }

    // --------------------------------------------------------------------------------------------
    // Public methods
    // --------------------------------------------------------------------------------------------

    // REQUIRES: milliseconds >= 0
    // MODIFIES: this
    // EFFECTS: Adds the given milliseconds to the current elapsed time, but only until
    //          the elapsed time = the total time of the segment. Returns any unused milliseconds
    //          that were not added to the elapsed time (0 if all the milliseconds were used).
    public long addTime(long milliseconds) {
        long newTime = currentTime + milliseconds;
        currentTime = min(totalTime, newTime);
        return newTime - currentTime;
    }

    // REQUIRES: newTotalTime >= 0
    // MODIFIES: this
    // EFFECTS: Sets a new total time for the segment. Ensures that the current elapsed time
    //          does not exceed this new total time.
    public void setTotalTime(long newTotalTime) {
        totalTime = newTotalTime;
        currentTime = min(currentTime, newTotalTime);
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public long getTotalTime() {
        return totalTime;
    }

    // --------------------------------------------------------------------------------------------
    // Segment methods
    // --------------------------------------------------------------------------------------------

    @Override
    public SegmentType getType() {
        return SegmentType.TIME;
    }

    // EFFECTS: Returns if the segment is complete, i.e. the elapsed time is equal
    //          to the total time (or greater than just in case!)
    @Override
    public boolean isComplete() {
        return currentTime >= totalTime;
    }

    // MODIFIES: this
    // EFFECTS: Resets the segment to its initial state, i.e. no time elapsed
    @Override
    public void reset() {
        currentTime = 0;
    }

    // --------------------------------------------------------------------------------------------
    // Encodable methods
    // --------------------------------------------------------------------------------------------

    @Override
    public JSONObject encoded() {
        JSONObject object = new JSONObject();
        object.put(RoutineJsonKey.TYPE.toString(), getType().name());
        object.put(RoutineJsonKey.NAME.toString(), getName());
        object.put(RoutineJsonKey.TOTAL_TIME.toString(), totalTime);
        object.put(RoutineJsonKey.CURRENT_TIME.toString(), currentTime);
        return object;
    }
}

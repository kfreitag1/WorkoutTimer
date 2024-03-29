package model;

import org.json.JSONObject;
import persistence.RoutineJsonKey;

// Represents a Segment that is manually completed
// i.e. the segment is only complete when it is manually set to be complete
public class ManualSegment extends Segment {
    private boolean finished;

    // --------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------

    // EFFECTS: Constructs a new manual segment with the given name (not completed)
    public ManualSegment(String name) {
        this(name, false);
    }

    // EFFECTS: Constructs a potentially completed manual segment with the given name and completion state
    public ManualSegment(String name, boolean finished) {
        super(name);
        this.finished = finished;
    }

    // --------------------------------------------------------------------------------------------
    // Public methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: self
    // EFFECTS: Sets the segment to be complete
    public void setComplete() {
        finished = true;
    }

    // --------------------------------------------------------------------------------------------
    // Segment methods
    // --------------------------------------------------------------------------------------------

    @Override
    public SegmentType getType() {
        return SegmentType.MANUAL;
    }

    // EFFECTS: Returns true only if the segment is complete
    @Override
    public boolean isComplete() {
        return finished;
    }

    // MODIFIES: this
    // EFFECTS: Resets the segment to its initial state, i.e. not complete
    @Override
    public void reset() {
        finished = false;
    }

    // --------------------------------------------------------------------------------------------
    // Encodable methods
    // --------------------------------------------------------------------------------------------

    @Override
    public JSONObject encoded() {
        JSONObject object = new JSONObject();
        object.put(RoutineJsonKey.TYPE.toString(), getType().name());
        object.put(RoutineJsonKey.NAME.toString(), getName());
        object.put(RoutineJsonKey.FINISHED.toString(), finished);
        return object;
    }
}

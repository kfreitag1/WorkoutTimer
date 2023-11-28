package model;

import org.json.JSONObject;

// Represents a Segment that is manually completed
// i.e. the segment is only complete when it is manually set to be complete
public class ManualSegment implements Segment {
    private String name;
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
        this.name = name;
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
    public String getName() {
        return name;
    }

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

    @Override
    public void setName(String newName) {
        name = newName;
    }

    // --------------------------------------------------------------------------------------------
    // Encodable methods
    // --------------------------------------------------------------------------------------------

    @Override
    public JSONObject encoded() {
        JSONObject object = new JSONObject();
        object.put("type", getType().name());
        object.put("name", name);
        object.put("finished", finished);
        return object;
    }
}

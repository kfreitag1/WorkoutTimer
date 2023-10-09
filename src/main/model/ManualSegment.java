package model;

// Represents a Segment that is manually completed
// i.e. the segment is only complete when it is manually set to be complete
public class ManualSegment implements Segment {
    private String name;
    private boolean finished = false;

    // EFFECTS: Constructs a manual segment with the given name
    public ManualSegment(String name) {
        this.name = name;
    }

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
    public String getType() {
        return "manual";
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
}

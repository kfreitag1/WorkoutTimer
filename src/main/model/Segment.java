package model;

import persistence.Encodable;

// Classes which implement Segment represents one segment of a procedure in a
// Routine instance (or other class which implements SegmentGroup).
// I.e. Each segment of the procedure implements the following methods.
public abstract class Segment implements Encodable {
    private String name;

    // EFFECTS: Constructs a new segment with the given name
    public Segment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    // EFFECTS: Updates the state of the segment (only used for SegmentGroup instances)
    public void update() {
        // do nothing
    }

    // --------------------------------------------------------------------------------------------
    // Abstract methods
    // --------------------------------------------------------------------------------------------

    public abstract SegmentType getType();

    public abstract boolean isComplete();

    public abstract void reset();
}

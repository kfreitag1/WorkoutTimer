package model;

import persistence.Encodable;

// Classes which implement Segment represents one segment of a procedure in a
// Routine instance (or other class which implements SegmentList).
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

    // --------------------------------------------------------------------------------------------
    // Abstract methods
    // --------------------------------------------------------------------------------------------

    public abstract SegmentType getType();

    public abstract boolean isComplete();

    public abstract void reset();
}

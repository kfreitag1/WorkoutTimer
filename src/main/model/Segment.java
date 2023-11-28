package model;

import persistence.Encodable;

// Classes which implement Segment represents one segment of a procedure in a
// Routine instance (or other class which implements SegmentList).
// I.e. Each segment of the procedure implements the following methods.
public interface Segment extends Encodable {
    String getName();

    SegmentType getType();

    boolean isComplete();

    void reset();

    void setName(String newName);
}

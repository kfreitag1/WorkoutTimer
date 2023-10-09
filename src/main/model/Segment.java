package model;

// Classes which implement Segment represents one segment of a procedure in a
// Routine instance (or other class which implements SegmentList).
// I.e. Each segment of the procedure implements the following methods.
public interface Segment {
    String getName();

    String getType();

    boolean isComplete();

    void reset();

    void setName(String newName);
}

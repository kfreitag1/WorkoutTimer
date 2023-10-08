package model;

import java.util.List;

public interface SegmentList {
    public List<Segment> getSegments();

    public List<Segment> getFlattenedSegments();

    // returns the currently active segment (can be another repetition group)
    public Segment getCurrentSegment();

    public void reset();

    public boolean isComplete();

    public String getName();

    public void setName(String newName);
}

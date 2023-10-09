package model;

import java.util.List;

// Classes which implement SegmentList represents objects that contain a list
// of segments (instances of classes which implement Segment).
public interface SegmentList {
    List<Segment> getSegments();

    List<Segment> getFlattenedSegments();

    Segment getCurrentSegment();

    void reset();

    boolean isComplete();

    String getName();

    void setName(String newName);
}

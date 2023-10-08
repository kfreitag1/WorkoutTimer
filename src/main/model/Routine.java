package model;

// a list of timer segments, can play pause restart
// keeps track of current time (sec)

import java.util.ArrayList;
import java.util.List;

public class Routine implements SegmentList {
    private String name;
    private List<Segment> segments;
    // has a flattened numbering system that includes all segments in order, including subsegments of repeat ones


    // Public methods
    // --------------------------------------------------------------------------------------------

    public Routine(String name) {
        this.name = name;
        segments = new ArrayList<>();
    }

    public void addSegment(Segment segment) {
        segments.add(segment);
    }

    // requires that segment is in segments, if it was an only child of a RepeatSegment, remove the parent too
    public void removeSegment(Segment segment) {
        // TODO
    }

    //public void modifySegment(int index)

    // requires segments is not empty, isComplete() is false
    // the actual subsegment in a repeat
    public Segment getExactCurrentSegment() {
        Segment exactCurrentSegment = getCurrentSegment();

        // Check all the way down until not a repeat segment
        while (exactCurrentSegment instanceof RepeatSegment) {
            exactCurrentSegment = ((RepeatSegment) exactCurrentSegment).getCurrentSegment();
        }
        return exactCurrentSegment;
    }

    // SegmentList methods
    // --------------------------------------------------------------------------------------------

    @Override
    public List<Segment> getSegments() {
        // Update all the repeat segments if necessary
        for (Segment segment : segments) {
            if (segment instanceof RepeatSegment) {
                ((RepeatSegment) segment).updateRepeatCycle();
            }
        }
        return segments;
    }

    // makes repeats all in same list (including the repeats too! its everything)
    @Override
    public List<Segment> getFlattenedSegments() {
        List<Segment> allSegments = new ArrayList<>();
        for (Segment segment : getSegments()) {
            if (segment instanceof SegmentList) {
                allSegments.addAll(((SegmentList) segment).getFlattenedSegments());
            } else {
                allSegments.add(segment);
            }
        }
        return allSegments;
    }

    // requires that segments is not empty, isComplete() is false
    @Override
    public Segment getCurrentSegment() {
        for (Segment segment : segments) {
            if (!segment.isComplete()) {
                return segment;
            }
        }
        return null; // Will never reach here
    }

    @Override
    public void reset() {
        for (Segment segment : segments) {
            segment.reset();
        }
    }

    @Override
    public boolean isComplete() {
        if (segments.isEmpty()) {
            return true;
        }
        return segments.get(segments.size() - 1).isComplete();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String newName) {
        this.name = newName;
    }
}

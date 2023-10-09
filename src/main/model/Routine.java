package model;

// a list of timer segments, can play pause restart
// keeps track of current time (sec)

import java.util.ArrayList;
import java.util.List;

public class Routine implements SegmentList {
    private String name;
    private final List<Segment> segments;
    // has a flattened numbering system that includes all segments in order, including subsegments of repeat ones

    // --------------------------------------------------------------------------------------------
    // Public methods
    // --------------------------------------------------------------------------------------------

    public Routine(String name) {
        this.name = name;
        segments = new ArrayList<>();
    }

    public void addSegment(Segment segment) {
        segments.add(segment);
    }

    // requires that beforeSegment is in segments
    public void insertSegmentBefore(Segment newSegment, Segment segmentBeforeInserted) {
        // TODO
    }

    // requires that segment is in segments
    // if it was an only child of a RepeatSegment, remove the parent too
    public void removeSegment(Segment segment) {
        removeInSegmentList(segment, segments);
    }

    // requires that segment is in segmentList OR one of its children (somewhere)
    // modifies that list directly, either main or sublist
    // returns whether to CHECK if remove the parent too (if the child segment list is now empty)
    private boolean removeInSegmentList(Segment segmentToRemove, List<Segment> segmentList) {
        if (segmentList.remove(segmentToRemove)) {
            return segmentList.isEmpty();
        }

        // Search rest of the children/sub-children for it
        Segment parentToRemove = null;
        for (Segment child : segmentList) {
            if (child.getType().equals("repeat")) {
                List<Segment> grandchildren = ((RepeatSegment) child).getSegments();
                if (removeInSegmentList(segmentToRemove, grandchildren) && grandchildren.isEmpty()) {
                    parentToRemove = child;
                }
            }
        }

        // If removed parent segment without any children, check if you need to remove one layer up too
        return segmentList.remove(parentToRemove);
    }

    // requires that segmentToReplace is in segments
    public void replaceSegment(Segment newSegment, Segment segmentToReplace) {
        // TODO
    }

    public void advance(long milliseconds) {
        if (isComplete()) {
            return;
        }
        Segment currentSegment = getExactCurrentSegment();
        if (currentSegment.getType().equals("time")) {
            long remainingTime = ((TimeSegment) currentSegment).addTime(milliseconds);
            if (remainingTime != 0) {
                advance(remainingTime);
            }
        }
    }

    //public void modifySegment(int index)

    // requires isComplete() is false
    // the actual subsegment in a repeat
    public Segment getExactCurrentSegment() {
        Segment exactCurrentSegment = getCurrentSegment();

        // Check all the way down until not a repeat segment
        while (exactCurrentSegment instanceof RepeatSegment) {
            exactCurrentSegment = ((RepeatSegment) exactCurrentSegment).getCurrentSegment();
        }
        return exactCurrentSegment;
    }

    // --------------------------------------------------------------------------------------------
    // private methods
    // --------------------------------------------------------------------------------------------



    // --------------------------------------------------------------------------------------------
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
            allSegments.add(segment);
            if (segment.getType().equals("repeat")) {
                allSegments.addAll(((SegmentList) segment).getFlattenedSegments());
            }
        }
        return allSegments;
    }

    // requires that isComplete() is false
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

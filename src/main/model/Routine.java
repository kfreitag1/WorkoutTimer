package model;

import java.util.ArrayList;
import java.util.List;

// Represents a single routine which contains a procedure (list) of segments which can be,
// added, inserted, deleted, advanced (by time or manually).
public class Routine implements SegmentList {
    private String name;
    private final List<Segment> segments;

    // --------------------------------------------------------------------------------------------
    // Public methods
    // --------------------------------------------------------------------------------------------

    // EFFECTS: Constructs a routine with the given name and an empty list of segments.
    public Routine(String name) {
        this.name = name;
        segments = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: Adds the given segment to the end of segments
    public void addSegment(Segment segment) {
        segments.add(segment);
    }

    // REQUIRES: segmentToInsertBefore is in segments (or one of its children/sub-children!)
    // MODIFIES: this
    // EFFECTS: Inserts the given segment at the index in segments (or a sub-list) BEFORE the other
    //          specified segment.
    public void insertSegmentBefore(Segment segment, Segment segmentToInsertBefore) {
        insertInSegmentList(segment, segmentToInsertBefore, segments, false);
    }

    // REQUIRES: segmentToInsertAfter is in segments (or one of its children/sub-children!)
    // MODIFIES: this
    // EFFECTS: Inserts the given segment at the index in segments (or a sub-list) AFTER the other
    //          specified segment.
    public void insertSegmentAfter(Segment segment, Segment segmentToInsertAfter) {
        insertInSegmentList(segment, segmentToInsertAfter, segments, true);
    }

    // REQUIRES: segment in segments (or one of its children/sub-children!)
    // MODIFIES: this
    // EFFECTS: Removes the given segment from wherever it is in segments. ALSO removes any invalid
    //          repeat segments that would have no children after performing this operation.
    public void removeSegment(Segment segment) {
        removeInSegmentList(segment, segments);
    }

    // REQUIRES: milliseconds >= 0
    // MODIFIES: this
    // EFFECTS: Advances the current segment by the specified milliseconds if
    //          1. the routine is not complete, and 2. the current segment is a TimeSegment
    public void advance(long milliseconds) {
        advanceRoutine(milliseconds, false);
    }

    // MODIFIES: this
    // EFFECTS: Advances the current segment if 1. the routine is not complete, and
    //          2. the current segment is a ManualSegment
    public void advance() {
        advanceRoutine(0, true);
    }

    // REQUIRES: isComplete() is false
    // MODIFIES: this
    // EFFECTS: Returns the exact segment that is currently active (i.e. not a RepeatSegment).
    //          Ensures that all segments are updated.
    public Segment getExactCurrentSegment() {
        Segment exactCurrentSegment = getCurrentSegment();

        // Check all the way down until not a repeat segment
        while (exactCurrentSegment instanceof RepeatSegment) {
            exactCurrentSegment = ((RepeatSegment) exactCurrentSegment).getCurrentSegment();
        }
        return exactCurrentSegment;
    }

    // --------------------------------------------------------------------------------------------
    // Private methods
    // --------------------------------------------------------------------------------------------

    // REQUIRES: segment in segmentList (or one of its children/sub-children!)
    // MODIFIES: segmentList
    // EFFECTS: Removes the given segment from wherever it is in segmentList. ALSO removes any invalid
    //          repeat segments that would have no children after performing this operation. Returns whether
    //          to CHECK if you need to remove the parent too (i.e. child segment list is now empty).
    private boolean removeInSegmentList(Segment segmentToRemove, List<Segment> segmentList) {
        // Possible to remove the segment directly
        if (segmentList.remove(segmentToRemove)) {
            // Return true if this list is now empty (remove parent)
            return segmentList.isEmpty();
        }

        // Search rest of the children/sub-children for it
        Segment parentToRemove = null;
        for (Segment child : segmentList) {
            if (!child.getType().equals("repeat")) {
                continue;
            }
            if (removeInSegmentList(segmentToRemove, ((RepeatSegment) child).getSegments())) {
                parentToRemove = child;
                break;
            }
        }

        // Remove the parent if it needs to be done
        if (segmentList.remove(parentToRemove)) {
            // Return true if this list is now empty (remove grandparent)
            return segmentList.isEmpty();
        } else {
            return false;
        }
    }

    // REQUIRES: segmentToInsertAround is in segmentList (or one of its children/sub-children!)
    // MODIFIES: segmentList
    // EFFECTS: Inserts the given segment in segmentList (or a sub-list) at the index either
    //          before or after the other segment given (which is already in the list).
    private void insertInSegmentList(Segment segment, Segment segmentToInsertAround,
                                     List<Segment> segmentList, boolean insertAfter) {
        // Segment is directly in the given segmentList
        if (segmentList.contains(segmentToInsertAround)) {
            int index = segmentList.indexOf(segmentToInsertAround);
            segmentList.add(index + (insertAfter ? 1 : 0), segment);
            return;
        }

        // Search rest of children/sub-children for it
        for (Segment child : segmentList) {
            if (child.getType().equals("repeat")) {
                insertInSegmentList(segment, segmentToInsertAround,
                        ((RepeatSegment) child).getSegments(), insertAfter);
            }
        }
    }

    // REQUIRES: milliseconds >= 0
    // MODIFIES: this
    // EFFECTS: Advances the current segment by either time or manual activation if
    //          the routine is not complete yet
    private void advanceRoutine(long milliseconds, boolean advanceManual) {
        if (isComplete()) {
            return;
        }

        Segment currentSegment = getExactCurrentSegment();

        if (currentSegment.getType().equals("time")) {
            long remainingTime = ((TimeSegment) currentSegment).addTime(milliseconds);
            if (remainingTime != 0) {
                advance(remainingTime);
            }
        } else if (advanceManual && currentSegment.getType().equals("manual")) {
            ((ManualSegment) currentSegment).setComplete();
        }
    }

    // --------------------------------------------------------------------------------------------
    // SegmentList methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Returns the segments list, ensuring that all segments are updated.
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

    // MODIFIES: this
    // EFFECTS: Returns a new flattened list of the segments. I.e. the children segment list
    //          is a list that can contain other lists, a flattened list would be the one-dimensional
    //          list with each segment in order (depth-first traversal). Ensures all segments are
    //          updated as well.
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

    // REQUIRES: isComplete() is false;
    // MODIFIES: this
    // EFFECTS: Returns the segment that is currently active (MAY be a RepeatSegment).
    //          Ensures that all segments are updated.
    @Override
    public Segment getCurrentSegment() {
        for (Segment segment : getSegments()) {
            if (!segment.isComplete()) {
                return segment;
            }
        }
        throw new IllegalStateException("All segments were complete, violates requires clause");
    }

    // MODIFIES: this
    // EFFECTS: Resets all segments to their initial states.
    @Override
    public void reset() {
        for (Segment segment : segments) {
            segment.reset();
        }
    }

    // MODIFIES: this
    // EFFECTS: Returns if the segment is complete, i.e. either no segments or the last
    //          child segment is complete. Ensures all segments are updated.
    @Override
    public boolean isComplete() {
        if (segments.isEmpty()) {
            return true;
        }
        return getSegments().get(segments.size() - 1).isComplete();
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

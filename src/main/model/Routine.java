package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Encodable;
import persistence.RoutineJsonKey;

import java.util.List;

// Represents a single routine which contains a procedure (list) of segments which can be,
// added, inserted, deleted, advanced (by time or manually).
public class Routine extends SegmentGroup {
    // EFFECTS: Constructs a routine with the given name and an empty list of segments.
    public Routine(String name) {
        super(name);
    }

    // --------------------------------------------------------------------------------------------
    // Public methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Adds the given segment to the end of segments
    public void addSegment(Segment segment) {
        getSegments().add(segment);
        EventLog.getInstance().logEvent(new Event("Added a segment with name: " + segment.getName()));
    }

    // REQUIRES: segmentToInsertBefore is in segments (or one of its children/sub-children!)
    // MODIFIES: this
    // EFFECTS: Inserts the given segment at the index in segments (or a sub-list) BEFORE the other
    //          specified segment.
    public void insertSegmentBefore(Segment segment, Segment segmentToInsertBefore) {
        insertInSegmentList(segment, segmentToInsertBefore, getSegments(), false);
        EventLog.getInstance().logEvent(new Event(
                "Inserted new segment with name: " + segment.getName() + ", before segment: "
                        + segmentToInsertBefore.getName()));
    }

    // REQUIRES: segmentToInsertAfter is in segments (or one of its children/sub-children!)
    // MODIFIES: this
    // EFFECTS: Inserts the given segment at the index in segments (or a sub-list) AFTER the other
    //          specified segment.
    public void insertSegmentAfter(Segment segment, Segment segmentToInsertAfter) {
        insertInSegmentList(segment, segmentToInsertAfter, getSegments(), true);
        EventLog.getInstance().logEvent(new Event(
                "Inserted new segment with name: " + segment.getName() + ", after segment: "
                        + segmentToInsertAfter.getName()));
    }

    // REQUIRES: segment in segments (or one of its children/sub-children!)
    // MODIFIES: this
    // EFFECTS: Removes the given segment from wherever it is in segments. ALSO removes any invalid
    //          repeat segments that would have no children after performing this operation.
    public void removeSegment(Segment segment) {
        removeInSegmentList(segment, getSegments());
        EventLog.getInstance().logEvent(new Event("Removed segment with name: " + segment.getName()));
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

        // Check all the way down until not a SegmentGroup
        while (exactCurrentSegment instanceof SegmentGroup) {
            SegmentGroup segmentGroup = (SegmentGroup) exactCurrentSegment;
            exactCurrentSegment = segmentGroup.getCurrentSegment();
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
            if (!(child instanceof SegmentGroup)) {
                continue;
            }
            if (removeInSegmentList(segmentToRemove, ((SegmentGroup) child).getSegments())) {
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
            if (child instanceof SegmentGroup) {
                insertInSegmentList(segment, segmentToInsertAround,
                        ((SegmentGroup) child).getSegments(), insertAfter);
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

        if (advanceManual && currentSegment.getType() == SegmentType.MANUAL) {
            ((ManualSegment) currentSegment).setComplete();
        } else if (currentSegment.getType() == SegmentType.TIME) {
            long remainingTime = ((TimeSegment) currentSegment).addTime(milliseconds);
            if (remainingTime != 0) {
                advance(remainingTime);
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    // Segment implementation
    // --------------------------------------------------------------------------------------------

    @Override
    public SegmentType getType() {
        return SegmentType.ROUTINE;
    }

    // --------------------------------------------------------------------------------------------
    // Encodable implementation
    // --------------------------------------------------------------------------------------------

    @Override
    public JSONObject encoded() {
        JSONObject object = new JSONObject();
        object.put(RoutineJsonKey.NAME.toString(), getName());

        JSONArray segments = new JSONArray();
        for (Segment segment : getSegments()) {
            segments.put(segment.encoded());
        }
        object.put(RoutineJsonKey.SEGMENTS.toString(), segments);

        return object;
    }
}

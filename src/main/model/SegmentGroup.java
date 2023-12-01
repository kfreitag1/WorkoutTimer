package model;

import java.util.ArrayList;
import java.util.List;

// Classes which implement SegmentGroup represents objects that contain a list
// of segments (instances of classes which implement Segment).
public abstract class SegmentGroup extends Segment {
    private final List<Segment> children;

    // EFFECTS: Constructs a SegmentGroup with no children elements
    public SegmentGroup(String name) {
        this(name, new ArrayList<>());
    }

    // EFFECTS: Constructs a SegmentGroup with the given children elements
    public SegmentGroup(String name, List<Segment> children) {
        super(name);
        this.children = children;
    }

    // --------------------------------------------------------------------------------------------
    // Public methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Returns the list of child segments, ensuring they are all updated
    public List<Segment> getSegments() {
        update();
        return children;
    }

    // MODIFIES: this
    // EFFECTS: Returns a flattened list of all child segments, ensuring they are all updated.
    public List<Segment> getFlattenedSegments() {
        List<Segment> allSegments = new ArrayList<>();
        for (Segment segment : getSegments()) {
            allSegments.add(segment);
            if (segment instanceof SegmentGroup) {
                SegmentGroup segmentGroup = (SegmentGroup) segment;
                allSegments.addAll(segmentGroup.getFlattenedSegments());
            }
        }
        return allSegments;
    }

    // REQUIRES: isComplete() is false;
    // MODIFIES: this
    // EFFECTS: Returns the segment that is currently active. Ensures all segments are updated
    public Segment getCurrentSegment() {
        for (Segment segment : getSegments()) {
            if (!segment.isComplete()) {
                return segment;
            }
        }
        throw new IllegalStateException("All segments were complete, violates requires clause");
    }

    // --------------------------------------------------------------------------------------------
    // Segment implementation
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Resets all segments to their initial states.
    @Override
    public void reset() {
        for (Segment segment : children) {
            segment.reset();
        }
    }

    // MODIFIES: this
    // EFFECTS: Returns if the segment is complete, i.e. either no segments or the last
    //          child segment is complete.
    @Override
    public boolean isComplete() {
        if (children.isEmpty()) {
            return true;
        }
        return children.get(children.size() - 1).isComplete();
    }

    // MODIFIES: this
    // EFFECTS: Updates all children segments.
    @Override
    public void update() {
        for (Segment child : children) {
            child.update();
        }
    }
}

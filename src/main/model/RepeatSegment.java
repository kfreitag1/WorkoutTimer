package model;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

// Represents a Segment that repeats a list of children segments
// for a certain number of times
public class RepeatSegment implements Segment, SegmentList {
    private String name;
    private int numRepeats;
    private int currentCycle;

    // Will ALWAYS have at least one element
    private final List<Segment> children;

    // REQUIRES: numRepeats > 0, children.size() > 0
    // EFFECTS: Constructs a repeat segment with the given name, number of repetitions,
    //          and list of children Segments
    public RepeatSegment(String name, int numRepeats, List<Segment> children) {
        this.name = name;
        this.numRepeats = numRepeats;
        this.children = children;

        currentCycle = 1;
    }

    public int getTotalRepetitions() {
        return numRepeats;
    }

    // MODIFIES: this
    // EFFECTS: Returns the current repetition cycle. First makes sure that the current repetition
    //          is accurate.
    public int getCurrentRepetition() {
        incrementCycleIfNecessary();
        return currentCycle;
    }

    // MODIFIES: this
    // EFFECTS: Sets the new number of total repetitions. First makes sure that the current repetition
    //          is accurate, then ensures that the current repetition cycle does not exceed the new total.
    // NOTE: Will retain children segments, which may not be expected when newNumRepeats < currentCycle.
    //       E.g. If the segment was on cycle 9/12, some of the children will be incomplete. If the total
    //       number of repetitions is then set to something less than 9 (such as 4), then the segment will
    //       be on cycle 4/4 (the last cycle), but will still have the same incomplete children.
    public void setNewRepeats(int newNumRepeats) {
        incrementCycleIfNecessary();
        numRepeats = newNumRepeats;
        currentCycle = min(currentCycle, numRepeats);
    }

    // MODIFIES: this
    // EFFECTS: Modifies the current cycle number to ensure that it is accurate. Ensures all children are
    //          similarly updated to have accurate cycle numbers.
    public void updateRepeatCycle() {
        incrementCycleIfNecessary();
        for (Segment child : children) {
            if (child instanceof RepeatSegment) {
                ((RepeatSegment) child).updateRepeatCycle();
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    // Private methods
    // --------------------------------------------------------------------------------------------

    // REQUIRES: currentCycle < numRepeats
    // EFFECTS: Increments the current repetition cycle counter by one and resets all children
    private void incrementCycle() {
        currentCycle++;
        for (Segment child : children) {
            child.reset();
        }
    }

    // MODIFIES: this
    // EFFECTS: May increment the current cycle number to ensure that it is accurate (i.e. any mismatches between
    //          the completion states of the children and the current cycle number).
    private void incrementCycleIfNecessary() {
        if (currentCycle < numRepeats && children.get(children.size() - 1).isComplete()) {
            incrementCycle();
        }
    }

    // --------------------------------------------------------------------------------------------
    // Segment methods
    // --------------------------------------------------------------------------------------------

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "repeat";
    }

    // EFFECTS: Returns if the segment is complete, i.e. on the last cycle and the last
    //          child is also complete.
    @Override
    public boolean isComplete() {
        return currentCycle == numRepeats && children.get(children.size() - 1).isComplete();
    }

    // MODIFIES: this
    // EFFECTS: Resets the segment to its initial state, i.e. first cycle, all children reset.
    @Override
    public void reset() {
        currentCycle = 1;
        for (Segment child : children) {
            child.reset();
        }
    }

    @Override
    public void setName(String newName) {
        name = newName;
    }

    // --------------------------------------------------------------------------------------------
    // SegmentList methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Returns the list of children segments, ensuring that the current segment
    //          and all children are updated.
    @Override
    public List<Segment> getSegments() {
        updateRepeatCycle();
        return children;
    }

    // MODIFIES: this
    // EFFECTS: Returns a new flattened list of children segments. I.e. the children segment list
    //          is a list that can contain other lists, a flattened list would be the one-dimensional
    //          list with each segment in order (depth-first traversal). Ensures all segments are
    //          updated as well.
    @Override
    public List<Segment> getFlattenedSegments() {
        List<Segment> allSegments = new ArrayList<>();
        for (Segment child : getSegments()) {
            allSegments.add(child);
            if (child.getType().equals("repeat")) {
                allSegments.addAll(((RepeatSegment) child).getFlattenedSegments());
            }
        }
        return allSegments;
    }

    // REQUIRES: isComplete() is false
    // MODIFIES: this
    // EFFECTS: Returns the first child segment which is incomplete. First makes sure that the current
    //          repetition is accurate.
    @Override
    public Segment getCurrentSegment() {
        incrementCycleIfNecessary();
        for (Segment child : children) {
            if (!child.isComplete()) {
                return child;
            }
        }
        return null; // Will never reach here
    }
}

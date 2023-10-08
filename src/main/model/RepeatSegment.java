package model;

import java.util.ArrayList;
import java.util.List;

public class RepeatSegment implements Segment, SegmentList {
    private String name;
    private int numRepeats;
    private final List<Segment> children; // will always have at least 1 child

    private int currentCycle;

    public RepeatSegment(String name, int numRepeats, List<Segment> children) {
        this.name = name;
        this.numRepeats = numRepeats;
        this.children = children;

        currentCycle = 1;
    }

    public int getTotalRepetitions() {
        return numRepeats;
    }

    public int getCurrentRepetition() {
        incrementCycleIfNecessary();
        return currentCycle;
    }

    // requires newNumRepeats >= currentCycle (invalid cycle otherwise, i.e. not running)
    public void setNewRepeats(int newNumRepeats) {
        numRepeats = newNumRepeats;
    }

    // propagates an update throughout all children
    public void updateRepeatCycle() {
        incrementCycleIfNecessary();
        for (Segment child : children) {
            if (child instanceof RepeatSegment) {
                ((RepeatSegment) child).updateRepeatCycle();
            }
        }
    }

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

    @Override
    public boolean isComplete() {
        return currentCycle == numRepeats && children.get(children.size() - 1).isComplete();
    }

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

    // SegmentList methods
    // --------------------------------------------------------------------------------------------

    @Override
    public List<Segment> getSegments() {
        updateRepeatCycle();
        return children;
    }

    @Override
    public List<Segment> getFlattenedSegments() {
        List<Segment> allSegments = new ArrayList<>();
        for (Segment child : getSegments()) {
            if (child instanceof SegmentList) {
                allSegments.addAll(((SegmentList) child).getFlattenedSegments());
            } else {
                allSegments.add(child);
            }
        }
        return allSegments;
    }

    // requires that isComplete() is false
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

    // Private methods
    // --------------------------------------------------------------------------------------------

    // requires  currentCycle < numRepeats
    // adds one to the cycle counter and resets all the children
    private void incrementCycle() {
        currentCycle++;
        for (Segment child : children) {
            child.reset();
        }
    }

    // checks if there is a mismatch between children and the current cycle number
    private void incrementCycleIfNecessary() {
        if (currentCycle < numRepeats && children.get(children.size() - 1).isComplete()) {
            incrementCycle();
        }
    }
}

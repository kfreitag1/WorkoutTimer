package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.RoutineJsonKey;

import java.util.List;

import static java.lang.Math.min;

// Represents a Segment that repeats a list of children segments
// for a certain number of times
public class RepeatSegment extends SegmentGroup {
    private int numRepeats;
    private int currentCycle;

    // --------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------

    // REQUIRES: numRepeats > 0, children.size() > 0
    //           i.e. MUST have at least 1 child
    // EFFECTS: Constructs a repeat segment with the given name, number of repetitions,
    //          and list of children Segments (on first cycle)
    public RepeatSegment(String name, int numRepeats, List<Segment> children) {
        this(name, numRepeats, children, 1);
    }

    // REQUIRES: numRepeats > 0, children.size() > 0, 1 <= currentCycle <= numRepeats
    //           i.e. MUST have at least 1 child
    // EFFECTS: Constructs a partially completed repeat segment with the given name,
    //          number of repetitions, list of children Segments, and current cycle number
    public RepeatSegment(String name, int numRepeats, List<Segment> children, int currentCycle) {
        super(name, children);
        this.numRepeats = numRepeats;
        this.currentCycle = currentCycle;
    }

    // --------------------------------------------------------------------------------------------
    // Public methods
    // --------------------------------------------------------------------------------------------

    public int getTotalRepetitions() {
        return numRepeats;
    }

    // MODIFIES: this
    // EFFECTS: Returns the current repetition cycle. First makes sure that the current repetition
    //          is accurate.
    public int getCurrentRepetition() {
        update();
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
        update();
        numRepeats = newNumRepeats;
        currentCycle = min(currentCycle, numRepeats);
    }

    // --------------------------------------------------------------------------------------------
    // Private methods
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: May increment the current cycle number to ensure that it is accurate (i.e. any mismatches between
    //          the completion states of the children and the current cycle number). Increments the current
    //          repetition cycle counter by one and resets all children
    private void incrementCycleIfNecessary() {
        if (currentCycle < numRepeats && super.isComplete()) {
            currentCycle++;
            super.reset();
        }
    }

    // --------------------------------------------------------------------------------------------
    // Segment implementation
    // --------------------------------------------------------------------------------------------

    @Override
    public SegmentType getType() {
        return SegmentType.REPEAT;
    }

    // EFFECTS: Returns if the segment is complete, i.e. on the last cycle and the last
    //          child is also complete.
    @Override
    public boolean isComplete() {
        return currentCycle == numRepeats && super.isComplete();
    }

    // MODIFIES: this
    // EFFECTS: Resets the segment to its initial state, i.e. first cycle, all children reset.
    @Override
    public void reset() {
        currentCycle = 1;
        super.reset();
    }

    // MODIFIES: this
    // EFFECTS: Modifies the current cycle number to ensure that it is accurate. Ensures all children are
    //          similarly updated to have accurate cycle numbers.
    @Override
    public void update() {
        incrementCycleIfNecessary();
        super.update();
    }

    // --------------------------------------------------------------------------------------------
    // Encodable implementation
    // --------------------------------------------------------------------------------------------

    @Override
    public JSONObject encoded() {
        JSONObject object = new JSONObject();
        object.put(RoutineJsonKey.TYPE.toString(), getType().name());
        object.put(RoutineJsonKey.NAME.toString(), getName());
        object.put(RoutineJsonKey.TOTAL_REPETITIONS.toString(), numRepeats);
        object.put(RoutineJsonKey.CURRENT_REPETITIONS.toString(), currentCycle);

        JSONArray children = new JSONArray();
        for (Segment segment : getSegments()) {
            children.put(segment.encoded());
        }
        object.put(RoutineJsonKey.CHILDREN.toString(), children);

        return object;
    }
}

package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RepeatSegmentTest {
    private RepeatSegment r1, r2, r3, r31, r32, r4;
    private ManualSegment m1, m2;
    private TimeSegment t1, t2;

    @BeforeEach
    public void runBefore() {
        // RepeatSegment with only one child, 1 repetition
        m1 = new ManualSegment("child 1");
        r1 = new RepeatSegment("only child parent", 1, Arrays.asList(m1));

        // RepeatSegment with three children, 2 repetitions
        t1 = new TimeSegment("child 2", 2000);
        t2 = new TimeSegment("child 3", 1000);
        r2 = new RepeatSegment("parent of 3 kids", 2, Arrays.asList(m1, t1, t2));

        // RepeatSegment 4 layers deep, 2 repeats
        m2 = new ManualSegment("level 4");
        r32 = new RepeatSegment("level 3", 1, Arrays.asList(m2));
        r31 = new RepeatSegment("level 2", 1, Arrays.asList(r32));
        r3 = new RepeatSegment("level 1", 2, Arrays.asList(r31));

        // RepeatSegment with partially completed cycles
        r4 = new RepeatSegment("partially complete", 3, Arrays.asList(m1), 2);
    }

    @Test
    public void testConstruction() {
        assertEquals("only child parent", r1.getName());
        assertEquals("repeat", r1.getType());
        assertEquals(1, r1.getCurrentRepetition());
        assertEquals(1, r1.getTotalRepetitions());
        assertEquals(Arrays.asList(m1), r1.getSegments());
        assertFalse(r1.isComplete());

        assertEquals("parent of 3 kids", r2.getName());
        assertEquals("repeat", r2.getType());
        assertEquals(1, r2.getCurrentRepetition());
        assertEquals(2, r2.getTotalRepetitions());
        assertEquals(Arrays.asList(m1, t1, t2), r2.getSegments());
        assertFalse(r2.isComplete());

        assertEquals("level 1", r3.getName());
        assertEquals("repeat", r3.getType());
        assertEquals(1, r3.getCurrentRepetition());
        assertEquals(2, r3.getTotalRepetitions());
        assertEquals(Arrays.asList(r31), r3.getSegments());
        assertFalse(r3.isComplete());

        assertEquals("partially complete", r4.getName());
        assertEquals("repeat", r4.getType());
        assertEquals(2, r4.getCurrentRepetition());
        assertEquals(3, r4.getTotalRepetitions());
        assertEquals(Arrays.asList(m1), r4.getSegments());
        assertFalse(r4.isComplete());
    }

    @Test
    public void testSetName() {
        r1.setName("new name");
        assertEquals("new name", r1.getName());
    }

    @Test
    public void testSetNewRepeats() {
        // current cycle < new num repetitions
        r1.setNewRepeats(3);
        assertEquals(3, r1.getTotalRepetitions());
        assertEquals(1, r1.getCurrentRepetition());
        assertFalse(r1.isComplete());

        // current cycle = new num repetitions
        ((ManualSegment) r1.getSegments().get(0)).setComplete();
        r1.updateRepeatCycle(); // should be on cycle 2 now
        r1.setNewRepeats(2);
        assertEquals(2, r1.getTotalRepetitions());
        assertEquals(2, r1.getCurrentRepetition());
        assertFalse(r1.isComplete());

        // current cycle > new num repetitions
        r1.setNewRepeats(1);
        assertEquals(1, r1.getTotalRepetitions());
        assertEquals(1, r1.getCurrentRepetition());
        assertFalse(r1.isComplete());
    }

    @Test
    public void testIncrementCycleIfNecessary() {
        List<Segment> kids = r2.getSegments();

        // No children complete, does NOT need to increase cycle number
        r2.updateRepeatCycle();
        assertEquals(1, r2.getCurrentRepetition());
        assertFalse(r2.isComplete());

        // Only some children complete, does NOT need to increase cycle number
        ((ManualSegment) kids.get(0)).setComplete();          // first child complete
        ((TimeSegment) kids.get(1)).addTime(2000); // second child complete
        r2.updateRepeatCycle();
        assertEquals(1, r2.getCurrentRepetition());
        assertFalse(r2.isComplete());
        assertTrue(kids.get(0).isComplete()); // Only first two kids are complete
        assertTrue(kids.get(1).isComplete());
        assertFalse(kids.get(2).isComplete());

        // All children are complete, does need to increase
        ((TimeSegment) kids.get(2)).addTime(1000); // third child complete
        r2.updateRepeatCycle();
        assertEquals(2, r2.getCurrentRepetition());
        assertFalse(r2.isComplete());
        assertFalse(kids.get(0).isComplete()); // Should reset states of the kids
        assertFalse(kids.get(1).isComplete());
        assertFalse(kids.get(2).isComplete());

        // All children are complete in final stage, does NOT need to increase
        ((ManualSegment) kids.get(0)).setComplete();          // first child complete again
        ((TimeSegment) kids.get(1)).addTime(2000); // second child complete again
        ((TimeSegment) kids.get(2)).addTime(1000); // third child complete again
        r2.updateRepeatCycle();
        assertEquals(2, r2.getCurrentRepetition());
        assertTrue(r2.isComplete());
        assertTrue(kids.get(0).isComplete()); // Should reset states of the kids
        assertTrue(kids.get(1).isComplete());
        assertTrue(kids.get(2).isComplete());
    }

    @Test
    public void testReset() {
        List<Segment> kids = r2.getSegments();

        // Reset initial condition
        r2.reset();
        assertEquals(1, r2.getCurrentRepetition());
        assertEquals(2, r2.getTotalRepetitions());
        assertFalse(r2.isComplete());
        assertFalse(kids.get(0).isComplete());
        assertFalse(kids.get(1).isComplete());
        assertFalse(kids.get(2).isComplete());

        // Reset on first cycle, partial kids complete
        ((ManualSegment) kids.get(0)).setComplete();          // first child complete
        r2.reset();
        assertEquals(1, r2.getCurrentRepetition());
        assertEquals(2, r2.getTotalRepetitions());
        assertFalse(r2.isComplete());
        assertFalse(kids.get(0).isComplete());
        assertFalse(kids.get(1).isComplete());
        assertFalse(kids.get(2).isComplete());

        // Reset not first cycle, no kids complete
        ((ManualSegment) kids.get(0)).setComplete();          // first child complete
        ((TimeSegment) kids.get(1)).addTime(2000); // second child complete
        ((TimeSegment) kids.get(2)).addTime(1000); // third child complete
        r2.updateRepeatCycle(); // now on cycle 2/2
        r2.reset();
        assertEquals(1, r2.getCurrentRepetition());
        assertEquals(2, r2.getTotalRepetitions());
        assertFalse(r2.isComplete());
        assertFalse(kids.get(0).isComplete());
        assertFalse(kids.get(1).isComplete());
        assertFalse(kids.get(2).isComplete());

        // Reset not first cycle, partial kids complete
        ((ManualSegment) kids.get(0)).setComplete();          // first child complete
        ((TimeSegment) kids.get(1)).addTime(2000); // second child complete
        ((TimeSegment) kids.get(2)).addTime(1000); // third child complete
        r2.updateRepeatCycle(); // now on cycle 2/2
        ((ManualSegment) kids.get(0)).setComplete();          // first child complete again
        ((TimeSegment) kids.get(1)).addTime(2000); // second child complete again
        assertFalse(r2.isComplete());
        r2.reset();
        assertEquals(1, r2.getCurrentRepetition());
        assertEquals(2, r2.getTotalRepetitions());
        assertFalse(r2.isComplete());
        assertFalse(kids.get(0).isComplete());
        assertFalse(kids.get(1).isComplete());
        assertFalse(kids.get(2).isComplete());

        // Reset everything complete
        ((ManualSegment) kids.get(0)).setComplete();          // first child complete
        ((TimeSegment) kids.get(1)).addTime(2000); // second child complete
        ((TimeSegment) kids.get(2)).addTime(1000); // third child complete
        r2.updateRepeatCycle(); // now on cycle 2/2
        ((ManualSegment) kids.get(0)).setComplete();          // first child complete again
        ((TimeSegment) kids.get(1)).addTime(2000); // second child complete again
        ((TimeSegment) kids.get(2)).addTime(1000); // third child complete again
        assertTrue(r2.isComplete());
        r2.reset();
        assertEquals(1, r2.getCurrentRepetition());
        assertEquals(2, r2.getTotalRepetitions());
        assertFalse(r2.isComplete());
        assertFalse(kids.get(0).isComplete());
        assertFalse(kids.get(1).isComplete());
        assertFalse(kids.get(2).isComplete());
    }

    @Test
    public void testGetFlattenedSegments() {
        // Already flat
        assertEquals(Arrays.asList(m1, t1, t2), r2.getSegments());
        assertEquals(Arrays.asList(m1, t1, t2), r2.getFlattenedSegments());

        // Flatten 3 layer deep
        assertEquals(Arrays.asList(r31), r3.getSegments());
        assertEquals(Arrays.asList(r31, r32, m2), r3.getFlattenedSegments());
    }

    @Test
    public void testGetCurrentSegment() {
        // First child is incomplete
        assertEquals(m1, r2.getCurrentSegment());

        // Second child is incomplete
        ((ManualSegment) r2.getSegments().get(0)).setComplete();          // first child complete
        assertEquals(t1, r2.getCurrentSegment());

        // Last child is incomplete
        ((ManualSegment) r2.getSegments().get(0)).setComplete();          // first child complete
        ((TimeSegment) r2.getSegments().get(1)).addTime(2000); // second child complete
        ((TimeSegment) r2.getSegments().get(2)).addTime(200); // third child partially complete
        assertEquals(t2, r2.getCurrentSegment());

        // IMPOSSIBLE CONDITION ALL CHILDREN COMPLETE ON FINAL CYCLE (VIOLATES REQUIRES CLAUSE)
        ((TimeSegment) r2.getSegments().get(2)).addTime(800);  // third child complete
        r2.updateRepeatCycle();
        ((ManualSegment) r2.getSegments().get(0)).setComplete();          // first child complete again
        ((TimeSegment) r2.getSegments().get(1)).addTime(2000); // second child complete again
        ((TimeSegment) r2.getSegments().get(2)).addTime(1000); // third child complete again

        try {
            r2.getCurrentSegment();
            fail("Didn't go into illegal state!");
        } catch (IllegalStateException e) {
            // should be here
        }
    }
}

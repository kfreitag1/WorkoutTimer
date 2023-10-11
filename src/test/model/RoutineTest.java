package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class RoutineTest {
    private Routine rn1, rn2, rn3;
    private RepeatSegment r1, r2, r3;
    private ManualSegment m1, m2;
    private TimeSegment t1, t2;


    @BeforeEach
    public void runBefore() {
        // Set up segment children
        m1 = new ManualSegment("man 1");
        t1 = new TimeSegment("time 1", 2000);
        t2 = new TimeSegment("time 2", 1000);

        // RepeatSegment 4 layers deep, 2 repeats
        // (0) level 1 --- (1) level 2 --- (3) level 3 --- (4) man 2
        //             |-- (6) time 1  \-- (5) man 1
        //             \-- (7) time 2
        m2 = new ManualSegment("man 2");
        r3 = new RepeatSegment("level 3", 1, new ArrayList<>(Arrays.asList(m2)));
        r2 = new RepeatSegment("level 2", 1, new ArrayList<>(Arrays.asList(r3, m1)));
        r1 = new RepeatSegment("level 1", 2, new ArrayList<>(Arrays.asList(r2, t1, t2)));

        // SETUP ROUTINES

        // Routine with nothing
        rn1 = new Routine("empty routine");

        // Routine with single segment
        rn2 = new Routine("single routine");
        rn2.addSegment(m1);

        // Routine with repeat segment chain
        rn3 = new Routine("single routine");
        rn3.addSegment(r1);
    }

    @Test
    public void testConstruction() {
        assertEquals("empty routine", rn1.getName());
        assertTrue(rn1.getSegments().isEmpty());
        assertTrue(rn1.isComplete());
    }

    @Test
    public void testSetName() {
        rn1.setName("new name");
        assertEquals("new name", rn1.getName());
    }

    @Test
    public void testGetSegments() {
        assertEquals(Arrays.asList(r1), rn3.getSegments());
        assertEquals(Arrays.asList(r1, r2, r3, m2, m1, t1, t2), rn3.getFlattenedSegments());
    }

    @Test
    public void testGetCurrentSegment() {
        // Top level
        assertEquals(m1, rn2.getCurrentSegment());
        assertEquals(m1, rn2.getExactCurrentSegment());

        // In sublevel
        // Add an element in index 0 of rn3
        ManualSegment m3 = new ManualSegment("test");
        rn3.insertSegmentBefore(m3, r1);
        assertEquals(m3, rn3.getCurrentSegment());
        assertEquals(m3, rn3.getExactCurrentSegment());
        m3.setComplete();
        assertEquals(r1, rn3.getCurrentSegment());
        assertEquals(m2, rn3.getExactCurrentSegment()); // all the way at the bottom!
    }

    @Test
    public void testAddSegment() {
        ManualSegment m3 = new ManualSegment("test");

        rn1.addSegment(m3);
        assertEquals(Arrays.asList(m3), rn1.getSegments());

        rn2.addSegment(m3);
        assertEquals(Arrays.asList(m1, m3), rn2.getSegments());
    }

    @Test
    public void testInsertSegmentBefore() {
        ManualSegment m3 = new ManualSegment("test");
        ManualSegment m4 = new ManualSegment("test2");

        // Top level
        rn3.insertSegmentBefore(m3, r1);
        assertEquals(Arrays.asList(m3, r1), rn3.getSegments());

        // In sublevel
        rn3.insertSegmentBefore(m4, r2);
        assertEquals(Arrays.asList(m3, r1), rn3.getSegments());
        assertEquals(Arrays.asList(m3, r1, m4, r2, r3, m2, m1, t1, t2), rn3.getFlattenedSegments());

    }

    @Test
    public void testInsertSegmentAfter() {
        ManualSegment m3 = new ManualSegment("test");
        ManualSegment m4 = new ManualSegment("test2");

        // Top level
        rn3.insertSegmentAfter(m3, r1);
        assertEquals(Arrays.asList(r1, m3), rn3.getSegments());

        // In sublevel
        rn3.insertSegmentAfter(m4, r2);
        assertEquals(Arrays.asList(r1, m3), rn3.getSegments());
        assertEquals(Arrays.asList(r1, r2, r3, m2, m1, m4, t1, t2, m3), rn3.getFlattenedSegments());
    }

    @Test
    public void testRemoveSegment() {
        // Remove on top level
        rn2.removeSegment(m1);
        assertTrue(rn2.getSegments().isEmpty());

        // Remove in sub level, don't remove parent
        rn3.removeSegment(t1);
        assertEquals(Arrays.asList(r1, r2, r3, m2, m1, t2), rn3.getFlattenedSegments());

        // Remove in sub level, remove parent
        rn3.removeSegment(m2);
        assertEquals(Arrays.asList(r1, r2, m1, t2), rn3.getFlattenedSegments());

        // Remove in sub level, remove multiple parents
        rn3.removeSegment(t2);
        assertEquals(Arrays.asList(r1, r2, m1), rn3.getFlattenedSegments());
        rn3.removeSegment(m1);
        assertTrue(rn2.getSegments().isEmpty());
    }

    @Test
    public void testAdvance() {
        // Already complete
        rn1.advance();
        assertTrue(rn1.isComplete());

        // Advance time, wrong segment (expect manual, given time)
        rn2.advance(2000);
        assertEquals(m1, rn2.getCurrentSegment());
        assertFalse(rn2.isComplete());

        // Advance manual segment, to completion
        rn2.advance();
        assertTrue(rn2.isComplete());

        // Advance time, partially fill segment
        m2.setComplete(); // set up (m1 is already complete previously)
        rn3.advance(500);
        assertEquals(t1, rn3.getExactCurrentSegment());
        assertEquals(500, t1.getCurrentTime());

        // Advance manual, wrong segment (expect time, given manual)
        rn3.advance();
        assertEquals(t1, rn3.getExactCurrentSegment());
        assertEquals(500, t1.getCurrentTime());

        // Advance time, roll over time to new segment
        rn3.advance(1800);
        assertEquals(t2, rn3.getExactCurrentSegment());
        assertEquals(300, t2.getCurrentTime());
    }

    @Test
    public void testReset() {
        rn3.advance();
        rn3.advance();
        rn3.advance(500 + 1800);
        assertEquals(t2, rn3.getExactCurrentSegment());

        rn3.reset();
        assertEquals(m2, rn3.getExactCurrentSegment());
    }
}

package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimeSegmentTest {
    private TimeSegment t1, t2, t3, t4;

    @BeforeEach
    public void runBefore() {
        t1 = new TimeSegment("no time", 0);
        t2 = new TimeSegment("two seconds", 2000);
        t3 = new TimeSegment("three and half seconds", 3500);
        t4 = new TimeSegment("partially complete", 100, 90);
    }

    @Test
    public void testConstruction() {
        assertEquals("no time", t1.getName());
        assertEquals(SegmentType.TIME, t1.getType());
        assertEquals(0, t1.getTotalTime());
        assertEquals(0, t1.getCurrentTime());
        assertTrue(t1.isComplete());

        assertEquals("two seconds", t2.getName());
        assertEquals(SegmentType.TIME, t2.getType());
        assertEquals(2000, t2.getTotalTime());
        assertEquals(0, t2.getCurrentTime());
        assertFalse(t2.isComplete());

        assertEquals("partially complete", t4.getName());
        assertEquals(SegmentType.TIME, t4.getType());
        assertEquals(100, t4.getTotalTime());
        assertEquals(90, t4.getCurrentTime());
        assertFalse(t4.isComplete());
    }
    
    @Test
    public void testSetName() {
        t1.setName("new name");
        assertEquals("new name", t1.getName());
    }

    @Test
    public void testAddTime() {
        // Add once, fill up partially from empty state
        long extraTime1 = t2.addTime(300);
        assertEquals(0, extraTime1);
        assertEquals(300, t2.getCurrentTime());
        assertFalse(t2.isComplete());
        
        // Add second time, fill up partially from partial state
        long extraTime2 = t2.addTime(400);
        assertEquals(0, extraTime2);
        assertEquals(700, t2.getCurrentTime());
        assertFalse(t2.isComplete());

        // Add third time, fill up completely, exactly
        long extraTime3 = t2.addTime(1300);
        assertEquals(0, extraTime3);
        assertEquals(2000, t2.getCurrentTime());
        assertTrue(t2.isComplete());

        // Add fourth time, try to add to full timer
        long extraTime4 = t2.addTime(25);
        assertEquals(25, extraTime4);
        assertEquals(2000, t2.getCurrentTime());
        assertTrue(t2.isComplete());

        // Second example, fill up completely and overshoot
        long extraTime5 = t3.addTime(4000);
        assertEquals(500, extraTime5);
        assertEquals(3500, t3.getCurrentTime());
        assertTrue(t3.isComplete());
    }

    // On empty one, currenttime = new time, currentime > new time
    @Test
    public void testSetTotalTime() {
        // On empty timer (0 for current time)
        t1.setTotalTime(400);
        assertEquals(400, t1.getTotalTime());
        assertEquals(0, t1.getCurrentTime());
        assertFalse(t1.isComplete());
        
        // On timer where current time < new time
        t1.addTime(200);
        t1.setTotalTime(300);
        assertEquals(300, t1.getTotalTime());
        assertEquals(200, t1.getCurrentTime());
        assertFalse(t1.isComplete());
        
        // On timer where current time = new time
        t1.setTotalTime(200);
        assertEquals(200, t1.getTotalTime());
        assertEquals(200, t1.getCurrentTime());
        assertTrue(t1.isComplete());

        // On timer where current time > new time
        t1.setTotalTime(150);
        assertEquals(150, t1.getTotalTime());
        assertEquals(150, t1.getCurrentTime());
        assertTrue(t1.isComplete());
    }

    @Test
    public void testReset() {
        // Reset empty timer (with 0 total time)
        t1.reset();
        assertEquals(0, t1.getCurrentTime());
        assertEquals(0, t1.getTotalTime());
        assertTrue(t1.isComplete());
        
        // Reset empty timer (with nonzero total time)
        t2.reset();
        assertEquals(0, t2.getCurrentTime());
        assertEquals(2000, t2.getTotalTime());
        assertFalse(t2.isComplete());

        // Reset partially filled timer
        t2.addTime(1000);
        assertEquals(1000, t2.getCurrentTime());
        assertFalse(t2.isComplete());
        t2.reset();
        assertEquals(0, t2.getCurrentTime());
        assertEquals(2000, t2.getTotalTime());
        assertFalse(t2.isComplete());
        
        // Reset full timer
        t2.addTime(2000);
        assertEquals(2000, t2.getCurrentTime());
        assertTrue(t2.isComplete());
        t2.reset();
        assertEquals(0, t2.getCurrentTime());
        assertEquals(2000, t2.getTotalTime());
        assertFalse(t2.isComplete());
        
    }
}

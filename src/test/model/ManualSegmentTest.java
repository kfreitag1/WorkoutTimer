package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManualSegmentTest {
    private ManualSegment m1;

    @BeforeEach
    public void runBefore() {
        m1 = new ManualSegment("test name");
    }

    @Test
    public void testConstruction() {
        assertEquals("manual", m1.getType());
        assertEquals("test name", m1.getName());
        assertFalse(m1.isComplete());
    }

    @Test
    public void testSetName() {
        m1.setName("new name");
        assertEquals("new name", m1.getName());
    }

    @Test
    public void testSetComplete() {
        m1.setComplete();
        assertTrue(m1.isComplete());
    }

    @Test
    public void testReset() {
        m1.setComplete();
        m1.reset();
        assertFalse(m1.isComplete());
    }
}

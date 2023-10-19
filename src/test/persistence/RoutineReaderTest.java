package persistence;

import model.ManualSegment;
import model.RepeatSegment;
import model.Routine;
import model.TimeSegment;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RoutineReaderTest {


    @Test
    void testReaderNonExistentFile() {
        RoutineReader reader = new RoutineReader("./data/noSuchFile.json");
        try {
            reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyRoutine() {
        RoutineReader reader = new RoutineReader("./data/testReaderEmptyRoutine.json");
        try {
            Routine routine = reader.read();
            assertEquals("Empty Name", routine.getName());
            assertTrue(routine.getSegments().isEmpty());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderInvalidRoutine() {
        RoutineReader reader = new RoutineReader("./data/testReaderInvalidRoutine.json");
        try {
            reader.read();
            fail("Should have thrown an exception when encountered invalid segment type");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderGeneralRoutine() {
        // Construct expected routine object to compare with the read one
        Routine expectedRoutine = new Routine("General Name");
        expectedRoutine.addSegment(new TimeSegment("Time1", 10000, 10000));
        expectedRoutine.addSegment(new RepeatSegment("Repeat1", 2, new ArrayList<>(Arrays.asList(
                new ManualSegment("Manual1", true),
                new RepeatSegment("Repeat2", 4, new ArrayList<>(Arrays.asList(
                        new ManualSegment("Manual2", true),
                        new TimeSegment("Time2", 4000, 2000)
                )), 2),
                new TimeSegment("Time3", 5000)
        ))));

        RoutineReader reader = new RoutineReader("./data/testReaderGeneralRoutine.json");
        try {
            Routine routine = reader.read();

            // Do a bunch of checks to make sure they're the same
            assertEquals(expectedRoutine.getName(), routine.getName());
            assertEquals(expectedRoutine.getSegments().size(), routine.getSegments().size());
            assertEquals(expectedRoutine.getFlattenedSegments().size(), routine.getFlattenedSegments().size());
            assertEquals(expectedRoutine.getSegments().get(0).getName(), routine.getSegments().get(0).getName());
            assertEquals(expectedRoutine.getExactCurrentSegment().getName(),
                    routine.getExactCurrentSegment().getName());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}

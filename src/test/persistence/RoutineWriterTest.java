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

public class RoutineWriterTest {


    @Test
    void testWriterInvalidFile() {
        try {
            RoutineWriter writer = new RoutineWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyWorkroom() {
        try {
            // Write the empty routine to a test file
            Routine routine = new Routine("Empty Routine");
            RoutineWriter writer = new RoutineWriter("./data/testWriterEmptyWorkroom.json");
            writer.open();
            writer.write(routine);
            writer.close();

            // Read the test file to make sure that it is good (COPIED FROM RoutineReader)
            RoutineReader reader = new RoutineReader("./data/testReaderEmptyRoutine.json");
            Routine readRoutine = reader.read();
            assertEquals("Empty Name", readRoutine.getName());
            assertTrue(readRoutine.getSegments().isEmpty());
        } catch (IOException e) {
            fail("Couldn't read from file... which it should have");
        }
    }

    @Test
    void testWriterGeneralWorkroom() {
        try {
            // Write routine to file
            Routine routine = new Routine("General Name");
            routine.addSegment(new TimeSegment("Time1", 10000, 10000));
            routine.addSegment(new RepeatSegment("Repeat1", 2, new ArrayList<>(Arrays.asList(
                    new ManualSegment("Manual1", true),
                    new RepeatSegment("Repeat2", 4, new ArrayList<>(Arrays.asList(
                            new ManualSegment("Manual2", true),
                            new TimeSegment("Time2", 4000, 2000)
                    )), 2),
                    new TimeSegment("Time3", 5000)
            ))));
            RoutineWriter writer = new RoutineWriter("./data/testWriterGeneralRoutine.json");
            writer.open();
            writer.write(routine);
            writer.close();

            // Read the test file to make sure that it is good (COPIED FROM RoutineReader)
            RoutineReader reader = new RoutineReader("./data/testReaderGeneralRoutine.json");
            Routine readRoutine = reader.read();
            // Do a bunch of checks to make sure they're the same
            assertEquals(routine.getName(), readRoutine.getName());
            assertEquals(routine.getSegments().size(), readRoutine.getSegments().size());
            assertEquals(routine.getFlattenedSegments().size(), readRoutine.getFlattenedSegments().size());
            assertEquals(routine.getSegments().get(0).getName(), readRoutine.getSegments().get(0).getName());
            assertEquals(routine.getExactCurrentSegment().getName(),
                    readRoutine.getExactCurrentSegment().getName());

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}

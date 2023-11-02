package ui.components;

import model.Routine;
import model.Segment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Represents a scrollable view that displays a Routine with a list of children Segments
public class RoutineDisplay extends JScrollPane {
    private final Routine routine;
    private final JPanel body;

    // EFFECTS: Constructs the routine display with the given routine and whether the routine
    //          is currently running or not
    public RoutineDisplay(Routine routine, boolean isRunning) {
        super(new JPanel(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.routine = routine;
        this.getVerticalScrollBar().setUnitIncrement(3);

        body = (JPanel) getViewport().getView();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        this.refresh(isRunning);
    }

    // MODIFIES: this
    // EFFECTS: Refreshes the routine display to show any changes made to the stored Routine
    //          objects. Completely replaces all the children SegmentDisplay views on each refresh
    public void refresh(boolean isRunning) {
        body.removeAll();
        for (Segment segment : routine.getSegments()) {
            SegmentDisplay segmentDisplay = new SegmentDisplay(routine, segment, isRunning);
            body.add(segmentDisplay);
        }
        repaint();
        revalidate();
    }
}

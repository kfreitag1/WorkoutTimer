package ui.components;

import model.Routine;
import model.Segment;

import javax.swing.*;
import java.util.List;

public class RoutineDisplay extends JPanel {
    private final Routine routine;

    public RoutineDisplay(Routine routine) {
        super();
        this.routine = routine;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.refresh();
    }

    public void refresh() {
        // TODO: May need to update the segments in a more smart way if this is really slow to remove
        //       and add them every update of the
        removeAll();
        for (Segment segment : routine.getSegments()) {
            SegmentDisplay segmentDisplay = new SegmentDisplay(segment);
            add(segmentDisplay);
        }
        revalidate();
    }


}

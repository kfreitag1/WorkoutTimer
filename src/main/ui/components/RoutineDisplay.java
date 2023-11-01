package ui.components;

import model.Routine;
import model.Segment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RoutineDisplay extends JScrollPane {
    private final Routine routine;

    private final JPanel body;

    public RoutineDisplay(Routine routine) {
        super(new JPanel(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.routine = routine;
        this.getVerticalScrollBar().setUnitIncrement(3);

        body = (JPanel) getViewport().getView();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        this.refresh();
    }

    public void refresh() {
        // TODO: May need to update the segments in a more smart way if this is really slow to remove
        //       and add them every update of the
        body.removeAll();
        for (Segment segment : routine.getSegments()) {
            SegmentDisplay segmentDisplay = new SegmentDisplay(segment);
            body.add(segmentDisplay);
        }
        repaint();
        revalidate();
    }
}

package ui.components;

import model.RepeatSegment;
import model.Segment;
import model.TimeSegment;

import javax.swing.*;

public class SegmentDisplay extends JPanel {
    private final Segment segment;

    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    SegmentDisplay(Segment segment) {
        super();
        this.segment = segment;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        add(new JLabel(segment.getName()));
        switch (segment.getType()) {
            case "manual":
                add(new JLabel("manual"));
                break;
            case "repeat":
                add(new JLabel("repeat"));
                add(new JLabel(((RepeatSegment) segment).getCurrentRepetition() + "/"
                        + ((RepeatSegment) segment).getTotalRepetitions()));
                JPanel childrenDisplay = new JPanel();
                childrenDisplay.setLayout(new BoxLayout(childrenDisplay, BoxLayout.PAGE_AXIS));
                for (Segment child : ((RepeatSegment) segment).getSegments()) {
                    childrenDisplay.add(new SegmentDisplay(child));
                }
                add(childrenDisplay);
                break;
            case "time":
                add(new JLabel("time"));
                add(new JLabel(((TimeSegment) segment).getCurrentTime() + "/"
                        + ((TimeSegment) segment).getTotalTime()));
                break;
        }

        revalidate();
    }
}

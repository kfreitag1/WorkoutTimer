package ui.components.routine;

import model.Routine;
import model.Segment;
import ui.components.ScrollableComponent;
import ui.handlers.SegmentMouseHandler;

import javax.swing.*;
import javax.swing.border.Border;

// Represents a scrollable view that displays a Routine with a list of children Segments
public class RoutineDisplay extends ScrollableComponent {
    private final Routine routine;
    private final SegmentMouseHandler mouseHandler;

    // EFFECTS: Constructs the routine display with the given routine, whether the routine
    //          is currently running or not, and a mouse handler to attach to the children segments
    public RoutineDisplay(Routine routine, String routineState, SegmentMouseHandler mouseHandler) {
        super();
        this.routine = routine;
        this.mouseHandler = mouseHandler;

        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        refresh(routineState);
    }

    // MODIFIES: this
    // EFFECTS: Refreshes the routine display to show any changes made to the stored Routine
    //          objects. Completely replaces all the children SegmentDisplay views on each refresh
    public void refresh(String routineState) {
        body.removeAll();
        if (routine.getSegments().isEmpty()) {
            addCenteredLabel("No segments yet!");
        } else {
            for (Segment segment : routine.getSegments()) {
                SegmentDisplay segmentDisplay = new SegmentDisplay(routine, segment, routineState, mouseHandler);
                body.add(segmentDisplay);
            }
        }
        repaint();
        revalidate();
    }

    // EFFECTS: Adds a JLabel with the specified text to the center of the screen
    private void addCenteredLabel(String text) {
        JPanel horizontalBox = new JPanel();
        horizontalBox.setLayout(new BoxLayout(horizontalBox, BoxLayout.LINE_AXIS));
        horizontalBox.add(Box.createHorizontalGlue());
        horizontalBox.add(new JLabel(text));
        horizontalBox.add(Box.createHorizontalGlue());

        body.add(Box.createVerticalGlue());
        body.add(horizontalBox);
        body.add(Box.createVerticalGlue());
        body.add(Box.createVerticalGlue());
    }

    // EFFECTS: Overrides the superclass border to remove it
    @Override
    public Border getBorder() {
        return BorderFactory.createEmptyBorder();
    }
}

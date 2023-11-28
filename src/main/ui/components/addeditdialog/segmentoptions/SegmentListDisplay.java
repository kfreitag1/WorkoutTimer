package ui.components.addeditdialog.segmentoptions;

import model.RepeatSegment;
import model.Segment;
import model.SegmentType;
import ui.components.ScrollableComponent;
import ui.components.addeditdialog.Updatable;
import ui.components.Validatable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

// Represents a view containing a list of child segments for a new/editing RepeatSegment
public class SegmentListDisplay extends ScrollableComponent implements Validatable {
    private final Updatable updateCallback;
    private final boolean isEditing;
    private final List<Segment> segments;

    // EFFECTS: Constructs a new segment list
    public SegmentListDisplay(Updatable updateCallback, Segment segmentToEdit) {
        super();
        this.updateCallback = updateCallback;
        isEditing = segmentToEdit != null;

        body.setLayout(new BoxLayout(body, BoxLayout.PAGE_AXIS));

        // If editing a pre-existing segment, set the child segment elements
        // otherwise make a new empty list
        if (isEditing && segmentToEdit.getType() == SegmentType.REPEAT) {
            segments = ((RepeatSegment) segmentToEdit).getSegments();
        } else {
            segments = new ArrayList<>();
        }

        updateLayout();
    }

    // MODIFIES: this
    // EFFECTS: Adds the given segment to the list, and performs any necessary updates
    public void addSegment(Segment segment) {
        segments.add(segment);
        updateLayout();
        updateCallback.update();
    }

    // REQUIRES: segment is in the list of segments
    // MODIFIES: this
    // EFFECTS: Removes the given segment from the list, and performs any necessary updates
    public void removeSegment(Segment segment) {
        segments.remove(segment);
        updateLayout();
        updateCallback.update();
    }

    // MODIFIES: this
    // EFFECTS: Updates the layout to match with the current list of segments
    private void updateLayout() {
        body.removeAll();
        for (Segment segment : segments) {
            // Horizontal group
            JPanel segmentContainer = new JPanel();
            segmentContainer.setLayout(new BoxLayout(segmentContainer, BoxLayout.LINE_AXIS));

            // Just display the segment name and a delete button
            segmentContainer.add(new JLabel(segment.getName()));
            segmentContainer.add(Box.createHorizontalGlue());
            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e -> removeSegment(segment));
            deleteButton.setEnabled(!isEditing); // Only enabled when not editing
            segmentContainer.add(deleteButton);

            body.add(segmentContainer);
        }
        revalidate();
        repaint();
    }

    public List<Segment> getSegments() {
        return segments;
    }

    // EFFECTS: Returns true of there is at least one child segment, since
    //          RepeatSegments cannot be empty
    @Override
    public boolean checkValid() {
        return !segments.isEmpty();
    }
}

package ui.components.addeditdialog.segmentoptions;

import model.RepeatSegment;
import model.Segment;
import model.SegmentType;
import ui.components.addeditdialog.Updatable;
import ui.components.ValidatedTextField;
import ui.screens.AddEditDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Represents an options panel for the construction/editing of a RepeatSegment
public class RepeatOptionsDisplay extends OptionsDisplay {
    private final ValidatedTextField cyclesEntry;
    private final SegmentListDisplay childrenSegments;

    // EFFECTS: Constructs a new display to gather the options for a RepeatSegment
    public RepeatOptionsDisplay(Updatable updateCallback, Segment segmentToEdit, JDialog parent) {
        super(updateCallback, segmentToEdit, parent);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // Label for number of cycles
        JLabel cyclesLabel = new JLabel("Number of cycles: (at least 1)");
        addAlignedComponent(cyclesLabel);

        // Validated text field for number of cycles
        // Set a default value if editing a segment
        String defaultCycles = "";
        if (segmentToEdit != null && segmentToEdit.getType() == SegmentType.REPEAT) {
            defaultCycles = Integer.toString(((RepeatSegment) segmentToEdit).getTotalRepetitions());
        }
        cyclesEntry = new ValidatedTextField("[1-9]\\d*", updateCallback, defaultCycles);
        addAlignedComponent(cyclesEntry);

        // Label for children segments
        JLabel childrenLabel = new JLabel("Segments to repeat:");
        addAlignedComponent(childrenLabel);

        // List of children segments
        childrenSegments = new SegmentListDisplay(updateCallback, segmentToEdit);
        addAlignedComponent(childrenSegments);

        // Add child segment button
        // Only enabled when making new segment, not editing
        JButton addSegmentButton = new JButton("Add Sub-Segment");
        addSegmentButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addSegmentButton.setEnabled(segmentToEdit == null);
        addSegmentButton.addActionListener(e -> {
            // Make another dialog box to make the new child segment!
            new AddEditDialog(parent, childrenSegments::addSegment);
        });
        add(addSegmentButton);
    }

    // MODIFIES: this
    // EFFECTS: Adds the given component to the layout, left aligned
    private void addAlignedComponent(JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(component);
    }

    // REQUIRES: checkValid is true
    // EFFECTS: Returns the number of cycles inputted by the user
    public int getNumCycles() {
        assert (checkValid());
        return Integer.parseInt(cyclesEntry.getText().trim());
    }

    // REQUIRES: checkValid is true
    // EFFECTS: Returns the child segments created by the user
    public List<Segment> getSegments() {
        assert (checkValid());
        return childrenSegments.getSegments();
    }

    // EFFECTS: Returns true if everything is valid
    @Override
    public boolean checkValid() {
        return cyclesEntry.checkValid() && childrenSegments.checkValid();
    }
}
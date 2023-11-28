package ui.screens;

import model.*;
import ui.components.Validatable;
import ui.components.ValidatedTextField;
import ui.components.addeditdialog.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

// Represents a dialog box to construct a new segment, or edit a pre-existing one
// Can be called from the main application, or recursively from another AddEditDialog box
// when creating child segments for a RepeatSegment!
public class AddEditDialog extends JDialog implements Updatable, Validatable {
    private static final int UNIT_SIZE = 8;

    private final SegmentReceiver receiver;
    private final Segment segmentToEdit;

    private final JPanel container = new JPanel();
    private ValidatedTextField nameField;
    private SegmentTypeOptions segmentTypeOptions;
    private JButton confirmButton;

    // --------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------

    // EFFECTS: Constructs a new 'editing' dialog box, using the given segment to edit.
    //          Can only be called from the root frame when the user clicks the edit button.
    public AddEditDialog(Frame parent, Segment segmentToEdit) {
        super(parent, "Editing: " + segmentToEdit.getName(), ModalityType.DOCUMENT_MODAL);
        this.receiver = null;
        this.segmentToEdit = segmentToEdit;
        init();

        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // EFFECTS: Constructs a new 'adding' dialog box from the main frame. When the new segment
    //          is created, it is passed to the function contained within the given receiver.
    public AddEditDialog(Frame parent, SegmentReceiver receiver) {
        super(parent, "Adding new segment", ModalityType.DOCUMENT_MODAL);
        this.receiver = receiver;
        this.segmentToEdit = null;
        init();

        setLocationRelativeTo(parent);
        setVisible(true);

    }

    // EFFECTS: Constructs a new 'adding' dialog box from another add dialog box (making a child
    //          segment for a RepeatSegment). When the new segment is created, it is passed to the
    //          function contained within the given receiver.
    public AddEditDialog(Dialog parent, SegmentReceiver receiver) {
        super(parent, "Adding new sub-segment", ModalityType.DOCUMENT_MODAL);
        this.receiver = receiver;
        this.segmentToEdit = null;
        init();

        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // --------------------------------------------------------------------------------------------
    // Helper initializers + layout
    // --------------------------------------------------------------------------------------------

    // MODIFIES: this
    // EFFECTS: Common initializer to lay out all the dialog elements
    private void init() {
        // Prevent the dialog box from being closed without the user pressing 'Cancel'
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Set the size of the dialog box
        setMinimumSize(new Dimension(300, 400));
        setSize(new Dimension(300, 400));

        // Make inner component with padding to put everything into
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        container.setBorder(new EmptyBorder(UNIT_SIZE, UNIT_SIZE, UNIT_SIZE, UNIT_SIZE));
        getContentPane().add(container);

        // Add name entry
        addAlignedComponent(new JLabel("Name: (cannot be empty)"));
        nameField = makeNameField();
        addAlignedComponent(nameField);

        // Add radio buttons to choose segment type
        addAlignedComponent(makeSegmentTypeChooser());

        // Add swappable pane for segment-specific options
        segmentTypeOptions = new SegmentTypeOptions(this, segmentToEdit, this);
        addAlignedComponent(segmentTypeOptions);

        // Add confirm and cancel buttons
        addAlignedComponent(makeCancelConfirmButtons());

        // Present to user
        update();
    }

    // EFFECTS: Returns the name creating field, with a default value if editing a segment
    private ValidatedTextField makeNameField() {
        String defaultValue = "";
        if (segmentToEdit != null) {
            defaultValue = segmentToEdit.getName();
        }

        return new ValidatedTextField(".+", this, defaultValue);
    }

    // EFFECTS: Returns a SegmentTypeChooser object with a specified action command to update
    //          the state of the segment option panel.
    private JComponent makeSegmentTypeChooser() {
        return new SegmentTypeChooser(e -> {
            SegmentType type = SegmentType.valueOf(e.getActionCommand());
            segmentTypeOptions.changeState(type);
        }, segmentToEdit);
    }

    // EFFECTS: Returns a panel containing the cancel and confirm buttons, with
    //          appropriate callbacks.
    private JComponent makeCancelConfirmButtons() {
        // Make cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            dispose();
        });

        // Make confirm button
        confirmButton = new JButton(segmentToEdit != null ? "Done" : "Add Segment");
        getRootPane().setDefaultButton(confirmButton);
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            // Dumb test to see if on Mac to set the text color on the default button
            // to white to match what is supposed to happen. For some reason Swing doesn't
            // do this already!
            confirmButton.setForeground(Color.white);
        }

        // Should only be able to be pressed when everything is in a valid state,
        // i.e. should be able to create/edit a segment without any errors
        confirmButton.addActionListener(e -> {
            if (receiver != null) {
                // Was adding new segment, so return the created segment
                receiver.receiveSegment(makeSegment());
            } else {
                // Was editing segment, so finalize the edits to the segment
                editSegment();
            }

            // Close the dialog box
            dispose();
        });

        // Pack buttons in a container
        JPanel cancelConfirmButtons = new JPanel();
        cancelConfirmButtons.setLayout(new BoxLayout(cancelConfirmButtons, BoxLayout.LINE_AXIS));
        cancelConfirmButtons.add(Box.createHorizontalGlue());
        cancelConfirmButtons.add(cancelButton);
        cancelConfirmButtons.add(confirmButton);
        return cancelConfirmButtons;
    }

    // MODIFIES: this
    // EFFECTS: Convenience function to add components to the root container, left aligned
    private void addAlignedComponent(JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(component);
    }

    // --------------------------------------------------------------------------------------------
    // Validatable, Updatable implementations
    // --------------------------------------------------------------------------------------------

    // EFFECTS: Returns true if everything (of the current segment type) is valid
    @Override
    public boolean checkValid() {
        boolean isNameValid = nameField.checkValid();
        boolean areSegmentOptionsValid = segmentTypeOptions.checkValid();

        return isNameValid && areSegmentOptionsValid;
    }

    // MODIFIES: this
    // EFFECTS: Called when a change is made to any element which could be valid/invalid,
    //          updates the dialog confirmation button in accordance to these changes.
    @Override
    public void update() {
        // Only enable confirm button if everything is valid
        confirmButton.setEnabled(checkValid());
    }

    // --------------------------------------------------------------------------------------------
    // Private completion methods
    // --------------------------------------------------------------------------------------------

    // REQUIRES: checkValid is true
    // EFFECTS: Makes and returns a new segment based on all the validated user input
    private Segment makeSegment() {
        assert (checkValid());

        String name = nameField.getText().trim();

        switch (segmentTypeOptions.getState()) {
            case TIME:
                long time = segmentTypeOptions.getTime();
                return new TimeSegment(name, time);
            case MANUAL:
                return new ManualSegment(name);
            case REPEAT:
                int numCycles = segmentTypeOptions.getNumCycles();
                List<Segment> children = segmentTypeOptions.getSegments();
                return new RepeatSegment(name, numCycles, children);
            default:
                throw new IllegalStateException("Segment type is invalid");
        }
    }

    // REQUIRES: checkValid is true, segmentToEdit is not null
    // EFFECTS: Edits the segment to edit based on all the validated user input
    private void editSegment() {
        assert (checkValid());
        assert (segmentToEdit != null);

        segmentToEdit.setName(nameField.getText().trim());

        switch (segmentTypeOptions.getState()) {
            case TIME:
                TimeSegment timeSegmentToEdit = (TimeSegment) segmentToEdit;
                timeSegmentToEdit.setTotalTime(segmentTypeOptions.getTime());
                break;
            case MANUAL:
                break;
            case REPEAT:
                RepeatSegment repeatSegmentToEdit = (RepeatSegment) segmentToEdit;
                repeatSegmentToEdit.setNewRepeats(segmentTypeOptions.getNumCycles());
                break;
            default:
                throw new IllegalStateException("Segment type is invalid");
        }
    }
}

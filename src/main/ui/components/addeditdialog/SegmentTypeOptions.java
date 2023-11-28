package ui.components.addeditdialog;

import model.Segment;
import model.SegmentType;
import ui.components.Validatable;
import ui.components.addeditdialog.segmentoptions.ManualOptionsDisplay;
import ui.components.addeditdialog.segmentoptions.RepeatOptionsDisplay;
import ui.components.addeditdialog.segmentoptions.TimeOptionsDisplay;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Represents a container view which can show the segment options view for all the
// segment types, uses a CardLayout to cycle between them.
public class SegmentTypeOptions extends JPanel implements Validatable {
    private final CardLayout layout = new CardLayout();
    private final Updatable updateCallback;

    private final TimeOptionsDisplay timeOptions;
    private final ManualOptionsDisplay manualOptions;
    private final RepeatOptionsDisplay repeatOptions;

    // Default state is time
    private SegmentType state = SegmentType.TIME;

    // EFFECTS: Constructs a new container view for the segment type options
    public SegmentTypeOptions(Updatable updateCallback, Segment segmentToEdit, JDialog parent) {
        super();
        this.updateCallback = updateCallback;
        setLayout(layout);

        // Construct the segment type option panels
        timeOptions = new TimeOptionsDisplay(updateCallback, segmentToEdit, parent);
        manualOptions = new ManualOptionsDisplay(updateCallback, segmentToEdit, parent);
        repeatOptions = new RepeatOptionsDisplay(updateCallback, segmentToEdit, parent);

        // Add them to the layout
        add(timeOptions, SegmentType.TIME.name());
        add(manualOptions, SegmentType.MANUAL.name());
        add(repeatOptions, SegmentType.REPEAT.name());

        // If editing a preexisting segment, set the panel to be its type,
        // otherwise it will be "time" by default for a new segment
        if (segmentToEdit != null) {
            setState(segmentToEdit.getType());
        }
    }

    // MODIFIES: this
    // EFFECTS: Changes the state (view displayed) and updates any callback objects
    public void changeState(SegmentType newState) {
        setState(newState);
        updateCallback.update();
    }

    // MODIFIES: this
    // EFFECTS: Internal helper to change the state and update the layout to display the
    //          options panel corresponding to the new state.
    private void setState(SegmentType newState) {
        state = newState;
        layout.show(this, newState.name());
    }

    public SegmentType getState() {
        return state;
    }

    // EFFECTS: Returns the validity of the options panel of segment type
    //          which is currently active.
    @Override
    public boolean checkValid() {
        switch (state) {
            case TIME:
                return timeOptions.checkValid();
            case MANUAL:
                return manualOptions.checkValid();
            case REPEAT:
                return repeatOptions.checkValid();
            default:
                throw new IllegalStateException("Invalid segment type");
        }
    }

    // REQUIRES: checkValid is true, state is "time"
    // EFFECTS: Returns the time inputted by the user, in milliseconds (pass through function
    //          to the time options panel).
    public long getTime() {
        assert (checkValid());
        return timeOptions.getTime();
    }

    // REQUIRES: checkValid is true, state is "repeat"
    // EFFECTS: Returns the number of cycles inputted by the user (pass through function
    //          to the repeat options panel).
    public int getNumCycles() {
        assert (checkValid());
        return repeatOptions.getNumCycles();
    }

    // REQUIRES: checkValid is true, state is "repeat"
    // EFFECTS: Returns the child segments created by the user (pass through function
    //          to the repeat options panel).
    public List<Segment> getSegments() {
        assert (checkValid());
        return repeatOptions.getSegments();
    }
}

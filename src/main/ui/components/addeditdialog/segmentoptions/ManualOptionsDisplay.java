package ui.components.addeditdialog.segmentoptions;

import model.Segment;
import ui.components.addeditdialog.Updatable;

import javax.swing.*;

// Represents an options panel for the construction/editing of a ManualSegment
// (Note: empty for now, but good to have in case want to add anything in future)
public class ManualOptionsDisplay extends OptionsDisplay {

    // EFFECTS: Constructs a new display to gather the options for a RepeatSegment
    public ManualOptionsDisplay(Updatable updateCallback, Segment segmentToEdit, JDialog parent) {
        super(updateCallback, segmentToEdit, parent);
    }

    // EFFECTS: Always valid, returns true
    @Override
    public boolean checkValid() {
        return true;
    }
}

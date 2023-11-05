package ui.components.addeditdialog.segmentoptions;

import model.Segment;
import ui.components.addeditdialog.Updatable;
import ui.components.Validatable;

import javax.swing.*;

// Represents a generic display panel which shows options for a segment type
public abstract class OptionsDisplay extends JPanel implements Validatable {
    protected Updatable updateCallback;
    protected Segment segmentToEdit;
    protected JDialog parentDialog;

    // EFFECTS: Constructs an options display panel with a given callback to be called
    //          when an update is make, an optional segment which represents the segment to
    //          edit (creating brand-new segment if null), and the parent JDialog object
    public OptionsDisplay(Updatable updateCallback, Segment segmentToEdit, JDialog parentDialog) {
        super();
        this.updateCallback = updateCallback;
        this.segmentToEdit = segmentToEdit;
        this.parentDialog = parentDialog;
    }
}

package ui.components.addeditdialog;

import model.Segment;
import model.SegmentType;

import javax.swing.*;
import java.awt.event.ActionListener;

// Represents a group of radio buttons to select the segment type
public class SegmentTypeChooser extends JPanel {
    private final ActionListener listener;

    private JRadioButton timeRadio;
    private JRadioButton manualRadio;
    private JRadioButton repeatRadio;

    // EFFECTS: Constructs a group of connected radio buttons to select the segment type
    public SegmentTypeChooser(ActionListener listener, Segment segmentToEdit) {
        super();
        this.listener = listener;

        initLayout();

        // Determine which radio button is selected, and whether they can be changed
        if (segmentToEdit != null) {
            switch (segmentToEdit.getType()) {
                case TIME:
                    timeRadio.setSelected(true);
                    break;
                case MANUAL:
                    manualRadio.setSelected(true);
                    break;
                case REPEAT:
                    repeatRadio.setSelected(true);
                    break;
            }

            // Don't let user change the segment type of editing segment
            timeRadio.setEnabled(false);
            manualRadio.setEnabled(false);
            repeatRadio.setEnabled(false);
        } else {
            // Default is time selected
            timeRadio.setSelected(true);
        }
    }

    // MODIFIES: this
    // EFFECTS: Lays out all the buttons in this view
    private void initLayout() {
        ButtonGroup segmentRadioGroup = new ButtonGroup();

        timeRadio = makeRadioButton("Time", SegmentType.TIME.name());
        add(timeRadio);
        segmentRadioGroup.add(timeRadio);

        manualRadio = makeRadioButton("Manual", SegmentType.MANUAL.name());
        add(manualRadio);
        segmentRadioGroup.add(manualRadio);

        repeatRadio = makeRadioButton("Repeat", SegmentType.REPEAT.name());
        add(repeatRadio);
        segmentRadioGroup.add(repeatRadio);
    }

    // EFFECTS: Returns a new JRadioButton with the given name as a label,
    //          and specified action command.
    private JRadioButton makeRadioButton(String name, String actionCommand) {
        JRadioButton button = new JRadioButton(name);
        button.setActionCommand(actionCommand);
        button.addActionListener(listener);
        return button;
    }
}

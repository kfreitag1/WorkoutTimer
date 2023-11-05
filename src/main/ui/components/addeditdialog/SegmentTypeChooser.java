package ui.components.addeditdialog;

import model.Segment;

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
                case "time":
                    timeRadio.setSelected(true);
                    break;
                case "manual":
                    manualRadio.setSelected(true);
                    break;
                case "repeat":
                    repeatRadio.setSelected(true);
                    break;
                default:
                    throw new IllegalStateException("Invalid segment type");
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

        timeRadio = makeRadioButton("Time", "time");
        add(timeRadio);
        segmentRadioGroup.add(timeRadio);

        manualRadio = makeRadioButton("Manual", "manual");
        add(manualRadio);
        segmentRadioGroup.add(manualRadio);

        repeatRadio = makeRadioButton("Repeat", "repeat");
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

package ui.components.routine;

import javax.swing.*;
import java.awt.*;

// Represents a toolbar button
public class ToolbarButton extends JButton {

    // EFFECTS: Constructs a regular button with specified label
    public ToolbarButton(String label) {
        super(label);
        init();
    }

    // EFFECTS: Constructs a square button with specified label and width/height
    public ToolbarButton(String label, int size) {
        super(label);
        init();

        // Force dimensions on all three since it is very finicky
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
        setPreferredSize(new Dimension(size, size));
    }

    // MODIFIES: this
    // EFFECTS: Common initializer for toolbar button construction
    private void init() {
        setAlignmentY(Component.BOTTOM_ALIGNMENT);
        setFocusable(false);
    }
}

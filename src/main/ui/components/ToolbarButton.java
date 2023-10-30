package ui.components;

import javax.swing.*;
import java.awt.*;

public class ToolbarButton extends JButton {

    // constructor for regular button
    public ToolbarButton(String label) {
        super(label);
        setAlignmentY(Component.BOTTOM_ALIGNMENT);
    }

    // constructor for square button
    public ToolbarButton(String label, int size) {
        super(label);
        setAlignmentY(Component.BOTTOM_ALIGNMENT);

        // Force dimensions on all three since it is very finicky
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
        setPreferredSize(new Dimension(size, size));
    }
}

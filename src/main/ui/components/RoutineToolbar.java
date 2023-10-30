package ui.components;

import javax.swing.*;
import java.awt.*;

public class RoutineToolbar extends JPanel {

    public RoutineToolbar() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        ToolbarButton playButton = new ToolbarButton("▶", 50); // PLAY, PAUSE: ⏸
        ToolbarButton restartButton = new ToolbarButton("⏮", 50); // RESTART
        ToolbarButton addButton = new ToolbarButton("Add");
        ToolbarButton deleteButton = new ToolbarButton("Delete");
        ToolbarButton editButton = new ToolbarButton("Edit");
        ToolbarButton saveButton = new ToolbarButton("Save");
        ToolbarButton closeButton = new ToolbarButton("Close");

        add(playButton);
        add(restartButton);
        add(Box.createHorizontalGlue());
        add(addButton);
        add(deleteButton);
        add(editButton);
        add(Box.createRigidArea(new Dimension(5, 1)));
        add(saveButton);
        add(closeButton);
    }
}

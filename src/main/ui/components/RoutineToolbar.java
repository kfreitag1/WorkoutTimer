package ui.components;

import ui.handlers.PlayPauseButtonHandler;
import ui.screens.RoutineScreen;

import javax.swing.*;
import java.awt.*;

public class RoutineToolbar extends JPanel {
    private final ToolbarButton playPauseButton;
    private final ToolbarButton restartButton;
    private final ToolbarButton addButton;
    private final ToolbarButton deleteButton;
    private final ToolbarButton editButton;
    private final ToolbarButton saveButton;
    private final ToolbarButton closeButton;

    private final RoutineScreen parentRoutineScreen;

    public RoutineToolbar(RoutineScreen parentRoutineScreen) {
        super();
        this.parentRoutineScreen = parentRoutineScreen;
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        //TODO Initialize all buttons with handlers
        playPauseButton = new ToolbarButton("▶", 50); // PLAY, PAUSE: ⏸
        restartButton = new ToolbarButton("⏮", 50); // RESTART
        addButton = new ToolbarButton("Add");
        deleteButton = new ToolbarButton("Delete");
        editButton = new ToolbarButton("Edit");
        saveButton = new ToolbarButton("Save");
        closeButton = new ToolbarButton("Close");

        // Add buttons to toolbar in specified order with spacing
        add(Box.createRigidArea(new Dimension(8, 8)));
        add(playPauseButton);
        add(restartButton);
        add(Box.createHorizontalGlue());
        add(addButton);
        add(deleteButton);
        add(editButton);
        add(saveButton);
        add(closeButton);
        add(Box.createRigidArea(new Dimension(8, 8)));

        initEventHandlers();
        updateToState("default");
    }

    private void initEventHandlers() {
        playPauseButton.addActionListener(new PlayPauseButtonHandler(parentRoutineScreen));
        closeButton.addActionListener(e -> parentRoutineScreen.close());
        restartButton.addActionListener(e -> parentRoutineScreen.resetRoutine());
        // TODO: other ones
    }

    // state is one of "default" "running" "editing"
    public void updateToState(String state) {
        switch (state) {
            case "default":
                updateToDefaultState();
                break;
            case "running":
                updateToRunningState();
                break;
            case "editing":
                updateToEditingState();
                break;
        }
    }

    private void updateToDefaultState() {
        playPauseButton.setText("▶");
        playPauseButton.setEnabled(true);
        restartButton.setEnabled(true);
        addButton.setEnabled(true);
        deleteButton.setEnabled(true);
        editButton.setEnabled(true);
        saveButton.setEnabled(true);
        closeButton.setEnabled(true);
    }

    private void updateToRunningState() {
        playPauseButton.setText("⏸");
        playPauseButton.setEnabled(true);
        restartButton.setEnabled(true);
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
        editButton.setEnabled(false);
        saveButton.setEnabled(false);
        closeButton.setEnabled(false);
    }

    private void updateToEditingState() {
        playPauseButton.setEnabled(false);
        restartButton.setEnabled(false);
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
        editButton.setEnabled(false);
        saveButton.setEnabled(false);
        closeButton.setEnabled(false);
    }
}

package ui.components.routine;

import ui.handlers.PlayPauseButtonHandler;
import ui.screens.RoutineScreen;
import ui.screens.RoutineScreenState;

import javax.swing.*;
import java.awt.*;

// Represents a toolbar for a RoutineScreen, displayed above the routine
public class RoutineToolbar extends JPanel {
    private static final int SQUARE_BUTTON_SIZE = 50;
    private static final int ICON_HEIGHT = 15;
    private static final int UNIT_SIZE = 8;

    private static final Icon PLAY_ICON = new PlayPauseRewindIcon(PlayPauseRewindIcon.Type.PLAY, ICON_HEIGHT);
    private static final Icon PAUSE_ICON = new PlayPauseRewindIcon(PlayPauseRewindIcon.Type.PAUSE, ICON_HEIGHT);
    private static final Icon REWIND_ICON = new PlayPauseRewindIcon(PlayPauseRewindIcon.Type.REWIND, ICON_HEIGHT);

    private final ToolbarButton playPauseButton;
    private final ToolbarButton restartButton;
    private final ToolbarButton addButton;
    private final ToolbarButton deleteButton;
    private final ToolbarButton editButton;
    private final ToolbarButton saveButton;
    private final ToolbarButton closeButton;

    private final RoutineScreen parentRoutineScreen;

    // EFFECTS: Constructs a new routine toolbar and initializes the appearance
    //          and functionality of all buttons
    public RoutineToolbar(RoutineScreen parentRoutineScreen) {
        super();
        this.parentRoutineScreen = parentRoutineScreen;

        // Create all buttons
        playPauseButton = new ToolbarButton(PLAY_ICON, SQUARE_BUTTON_SIZE);
        restartButton = new ToolbarButton(REWIND_ICON, SQUARE_BUTTON_SIZE);
        addButton = new ToolbarButton("Add");
        deleteButton = new ToolbarButton("Delete");
        editButton = new ToolbarButton("Edit");
        saveButton = new ToolbarButton("Save");
        closeButton = new ToolbarButton("Close");

        initLayout();
        initEventHandlers();
        updateToState(RoutineScreenState.DEFAULT);
    }

    // MODIFIES: this
    // EFFECTS: Lays out the components in the routine toolbar
    private void initLayout() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add buttons to toolbar in specified order with spacing
        add(Box.createRigidArea(new Dimension(UNIT_SIZE, 0)));
        add(playPauseButton);
        add(restartButton);
        add(Box.createHorizontalGlue());
        add(addButton);
        add(deleteButton);
        add(editButton);
        add(saveButton);
        add(closeButton);
        add(Box.createRigidArea(new Dimension(UNIT_SIZE, 0)));
    }

    // MODIFIES: this
    // EFFECTS: Adds "on click" event handlers or actions to all the buttons
    private void initEventHandlers() {
        playPauseButton.addActionListener(new PlayPauseButtonHandler(parentRoutineScreen));
        restartButton.addActionListener(e -> parentRoutineScreen.resetRoutine());
        addButton.addActionListener(e -> parentRoutineScreen.changeState(RoutineScreenState.ADDING));
        deleteButton.addActionListener(e -> parentRoutineScreen.changeState(RoutineScreenState.DELETING));
        editButton.addActionListener(e -> parentRoutineScreen.changeState(RoutineScreenState.EDITING));
        saveButton.addActionListener(e -> parentRoutineScreen.save());
        closeButton.addActionListener(e -> parentRoutineScreen.close());
    }

    // MODIFIES: this
    // EFFECTS: Modifies the appearance / interactivity of all the toolbar buttons
    //          based on the state of the RoutineScreen
    public void updateToState(RoutineScreenState state) {
        switch (state) {
            case DEFAULT:
                updateToDefaultState();
                break;
            case RUNNING:
                updateToRunningState();
                break;
            case EDITING:
            case DELETING:
            case ADDING:
                updateToChoosingState();
                break;
            default:
                throw new IllegalStateException("RoutineToolbar state is invalid");
        }
    }

    // MODIFIES: this
    // EFFECTS: Updates the buttons to a default state (all enabled, play button)
    private void updateToDefaultState() {
        playPauseButton.setIcon(PLAY_ICON);
        playPauseButton.setEnabled(true);
        restartButton.setEnabled(true);
        addButton.setEnabled(true);
        deleteButton.setEnabled(true);
        editButton.setEnabled(true);
        saveButton.setEnabled(true);
        closeButton.setEnabled(true);
    }

    // MODIFIES: this
    // EFFECTS: Updates the buttons to a running state (only pause button
    //          and restart button enabled)
    private void updateToRunningState() {
        playPauseButton.setIcon(PAUSE_ICON);
        playPauseButton.setEnabled(true);
        restartButton.setEnabled(true);
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
        editButton.setEnabled(false);
        saveButton.setEnabled(false);
        closeButton.setEnabled(false);
    }

    // MODIFIES: this
    // EFFECTS: Updates the buttons to a choosing state (all disabled)
    private void updateToChoosingState() {
        playPauseButton.setEnabled(false);
        restartButton.setEnabled(false);
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
        editButton.setEnabled(false);
        saveButton.setEnabled(false);
        closeButton.setEnabled(false);
    }
}

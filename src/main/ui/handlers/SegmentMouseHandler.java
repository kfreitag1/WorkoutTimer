package ui.handlers;

import ui.components.SegmentDisplay;
import ui.screens.RoutineScreen;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

// Represents a handler object for mouse clicks and movements
// Attaches to SegmentDisplay objects to capture these mouse events
public class SegmentMouseHandler extends RoutineHandler implements MouseMotionListener, MouseListener {
    public SegmentMouseHandler(RoutineScreen parentRoutineScreen) {
        super(parentRoutineScreen);
    }

    // MODIFIES: parentRoutineScreen
    // EFFECTS: Passes the segment click event to the RoutineScreen to handle
    @Override
    public void mousePressed(MouseEvent e) {
        SegmentDisplay segmentDisplay = (SegmentDisplay) e.getComponent();
        boolean clickedTopHalf = e.getY() < (segmentDisplay.getHeight() / 2);

        parentRoutineScreen.clickedSegmentLocation(segmentDisplay.getSegment(), clickedTopHalf);
    }

    // MODIFIES: the clicked SegmentDisplay
    // EFFECTS: Adds the mouse location to the clicked SegmentDisplay
    @Override
    public void mouseMoved(MouseEvent e) {
        setSegmentMouseLocation(e, false);
    }

    // MODIFIES: the clicked SegmentDisplay
    // EFFECTS: Removes the mouse location from the clicked SegmentDisplay
    @Override
    public void mouseExited(MouseEvent e) {
        setSegmentMouseLocation(e, true);
    }

    // MODIFIES: the clicked SegmentDisplay
    // EFFECTS: Convenience methods to add/remove the mouse location on the clicked SegmentDisplay
    private void setSegmentMouseLocation(MouseEvent e, boolean exited) {
        SegmentDisplay segmentDisplay = (SegmentDisplay) e.getComponent();
        segmentDisplay.setMouseLocation(exited ? null : new Dimension(e.getX(), e.getY()));
        segmentDisplay.repaint();
    }

    // --------------------------------------------------------------------------------------------
    // Unused overrides
    // --------------------------------------------------------------------------------------------

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }
}

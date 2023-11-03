package ui.components.routine;

import javax.swing.*;
import java.awt.*;

// Represents a view that contains a progress bar with a certain amount of completion
// The colour is set depending on the percentage and whether the bar is in an active state
// Meant to be a fixed representation of the progress bar, should be recreated when the segment changes
public class ProgressBar extends JComponent {
    private static final int UNIT_SIZE = 8;
    private static final int PROGRESS_BAR_HEIGHT = 12;
    private static final Color INACTIVE_PROGRESS_COLOR = new Color(170, 170, 170);
    private static final Color INCOMPLETE_PROGRESS_COLOR = new Color(210, 210, 210);
    private static final Color COMPLETE_PROGRESS_COLOR = new Color(148, 237, 145);
    private static final Color CURRENT_PROGRESS_COLOR = new Color(76, 167, 237);

    private final double percentage;
    private final boolean isActive;

    // REQUIRES: 0 <= percentage <= 1
    // EFFECTS: Constructs a progress bar with the given percentage complete, and
    //          whether it should display in an active state
    public ProgressBar(double percentage, boolean isActive) {
        super();
        this.percentage = percentage;
        this.isActive = isActive;
    }

    // EFFECTS: Sets the default size of the progress bar to be PROGRESS_BAR_HEIGHT high,
    //          and however wide it needs to be to fill up the container it's in
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, PROGRESS_BAR_HEIGHT);
    }

    // EFFECTS: Overrides the superclass painting method to draw the progress bar
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Have some spacing on either size of the progress bar
        int totalWidth = getWidth() - UNIT_SIZE * 2;

        // Draw either the background of the bar, or the full bar
        Color fullBarColor = (isActive ? COMPLETE_PROGRESS_COLOR : INACTIVE_PROGRESS_COLOR);
        g.setColor(percentage == 1.0 ? fullBarColor : INCOMPLETE_PROGRESS_COLOR);
        g.fillRoundRect(UNIT_SIZE, 0, totalWidth, getHeight(), UNIT_SIZE, UNIT_SIZE);

        // If the percentage is partially completed, draw the completion bar
        if (percentage > 0.0 && percentage < 1.0) {
            int completeWidth = (int) Math.round(totalWidth * percentage);
            g.setColor(isActive ? CURRENT_PROGRESS_COLOR : INACTIVE_PROGRESS_COLOR);
            g.fillRoundRect(UNIT_SIZE, 0, completeWidth, getHeight(), UNIT_SIZE, UNIT_SIZE);
        }
    }
}

package ui.components;

import model.RepeatSegment;
import model.Routine;
import model.Segment;
import model.TimeSegment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Represents a view that contains a Segment in a Routine
// Meant to be a fixed representation of the segment, should be recreated when the segment changes
public class SegmentDisplay extends JComponent {
    private static final int UNIT_SIZE = 8;
    private static final Color BORDER_COLOR = new Color(190, 190, 190);
    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color CURRENT_BACKGROUND_COLOR = new Color(225, 239, 252);
    private static final Color COMPLETE_BACKGROUND_COLOR = new Color(226, 252, 225);

    private final Routine routine;
    private final Segment segment;
    private String segmentState; // one of "default" "current" "complete"
    private final boolean isRunning;

    private JLabel infoText = new JLabel();

    // REQUIRES: segment is in routine
    // EFFECTS: Constructs a view for a given segment in the given routine,
    //          modifies the display when the timer is running (isRunning is true)
    SegmentDisplay(Routine routine, Segment segment, boolean isRunning) {
        super();
        this.segment = segment;
        this.routine = routine;
        this.isRunning = isRunning;

        determineState();
        initLayout();
    }

    // MODIFIES: this
    // EFFECTS: Sets the layout of this view and all the children views
    private void initLayout() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new EmptyBorder(UNIT_SIZE / 2, UNIT_SIZE, UNIT_SIZE / 2, UNIT_SIZE));
        setOpaque(false);

        // Top spacing
        add(Box.createRigidArea(new Dimension(0, UNIT_SIZE)));

        initTopSection();
        switch (segment.getType()) {
            case "manual":
                initManualSegment();
                break;
            case "repeat":
                initRepeatSegment();
                break;
            case "time":
                initTimeSegment();
                break;
        }

        // Bottom spacing
        add(Box.createRigidArea(new Dimension(0, UNIT_SIZE / 2)));
    }

    // MODIFIES: this
    // EFFECTS: Sets the layout for the top section of the segment view, contains
    //          the segment name and additional segment specific info (e.g. time)
    private void initTopSection() {
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.LINE_AXIS));
        topSection.setOpaque(false);

        topSection.add(Box.createRigidArea(new Dimension(UNIT_SIZE, 0)));
        topSection.add(new JLabel(segment.getName()));
        topSection.add(Box.createHorizontalGlue());
        topSection.add(infoText);
        topSection.add(Box.createRigidArea(new Dimension(UNIT_SIZE, 0)));

        add(topSection);
    }

    // MODIFIES: this
    // EFFECTS: Sets the layout for the bottom section specific for a manual segment
    private void initManualSegment() {
        if (isRunning && segmentState.equals("current")) {
            infoText.setText("Press space to complete!");
        }
    }

    // MODIFIES: this
    // EFFECTS: Sets the layout for the bottom section specific for a time segment
    private void initTimeSegment() {
        TimeSegment timeSegment = (TimeSegment) segment;
        long currentTime = timeSegment.getCurrentTime();
        long totalTime = timeSegment.getTotalTime();

        // Set the time in MM:SS TODO
        infoText.setText(timeSegment.getCurrentTime() + "/" + timeSegment.getTotalTime());

        // Progress bar in continuous amount
        add(Box.createRigidArea(new Dimension(0, UNIT_SIZE / 2)));
        add(new ProgressBar(getAccuratePercentage(currentTime, totalTime), isRunning));
    }

    // MODIFIES: this
    // EFFECTS: Sets the layout for the bottom section specific for a repeat segment
    private void initRepeatSegment() {
        RepeatSegment repeatSegment = (RepeatSegment) segment;
        int currentCycle = repeatSegment.getCurrentRepetition();
        int totalCycles = repeatSegment.getTotalRepetitions();

        // Set the current cycle as the info text
        infoText.setText(currentCycle + "/" + totalCycles);

        // Percentage bar in discrete amounts
        double percentage = 1.0;
        if (!segmentState.equals("complete")) {
            percentage = getAccuratePercentage(currentCycle - 1, totalCycles);
        }
        add(Box.createRigidArea(new Dimension(0, UNIT_SIZE / 2)));
        add(new ProgressBar(percentage, isRunning));

        // Children segments
        JPanel childrenDisplay = new JPanel();
        childrenDisplay.setOpaque(false);
        childrenDisplay.setLayout(new BoxLayout(childrenDisplay, BoxLayout.PAGE_AXIS));
        for (Segment child : repeatSegment.getSegments()) {
            childrenDisplay.add(new SegmentDisplay(routine, child, isRunning));
        }
        add(childrenDisplay);
    }

    // MODIFIES: this
    // EFFECTS: Sets the current state depending on whether the segment for this view
    //          is complete or if its currently active
    private void determineState() {
        if (segment.isComplete()) {
            segmentState = "complete";
        } else if (segment.equals(routine.getExactCurrentSegment())) {
            segmentState = "current";
        } else {
            segmentState = "default";
        }
    }

    // REQUIRES: b >= a, b != 0
    // EFFECTS: Returns the percentage of a and b (a/b) as a double, avoid any issues with float
    //          conversion by explicitly checking the edge cases where the percentage is 0 or 1
    private double getAccuratePercentage(long a, long b) {
        if (a == 0) {
            return 0.0;
        } else if (a == b) {
            return 1.0;
        } else {
            return ((double) a) / b;
        }
    }

    // EFFECTS: Sets the maximum size of the segment view to be as wide as possible,
    //          but only as tall as it needs to fit everything inside it
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
    }

    // EFFECTS: Overrides the superclass painting method to draw the segment view background
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int boxWidth = getWidth() - 2 * UNIT_SIZE;
        int boxHeight = getHeight() - UNIT_SIZE;

        Color backgroundColor = DEFAULT_BACKGROUND_COLOR;
        if (isRunning) {
            switch (segmentState) {
                case "complete":
                    backgroundColor = COMPLETE_BACKGROUND_COLOR;
                    break;
                case "current":
                    backgroundColor = CURRENT_BACKGROUND_COLOR;
                    break;
            }
        }

        // Fill in background color
        g.setColor(backgroundColor);
        g.fillRoundRect(UNIT_SIZE, UNIT_SIZE / 2, boxWidth, boxHeight, UNIT_SIZE, UNIT_SIZE);

        // Draw border around everything
        g.setColor(BORDER_COLOR);
        g.drawRoundRect(UNIT_SIZE, UNIT_SIZE / 2, boxWidth, boxHeight, UNIT_SIZE, UNIT_SIZE);
    }
}

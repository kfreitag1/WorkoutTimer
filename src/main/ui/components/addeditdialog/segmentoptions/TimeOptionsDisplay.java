package ui.components.addeditdialog.segmentoptions;

import model.Segment;
import ui.components.addeditdialog.Updatable;
import ui.components.ValidatedTextField;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Represents an options panel for the construction/editing of a TimeSegment
public class TimeOptionsDisplay extends OptionsDisplay {
    private final ValidatedTextField timeEntry;

    // EFFECTS: Constructs a new display to gather the options for a TimeSegment
    public TimeOptionsDisplay(Updatable updateCallback, Segment segmentToEdit, JDialog parent) {
        super(updateCallback, segmentToEdit, parent);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // Label for time entry
        JLabel timeLabel = new JLabel("Enter time: (e.g. 10s, 30, 3m, 3m20s)");
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(timeLabel);

        // Validated time entry field
        timeEntry = new ValidatedTextField(
                "((\\d+\\s*m\\s*)?\\d+\\s*s?|\\d+\\s*m)", updateCallback, "");
        // TODO: maybe want to add default value for segment to edit in future
        timeEntry.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(timeEntry);
    }

    // REQUIRES: checkValid is true
    // EFFECTS: Returns the time inputted by the user, in milliseconds
    public long getTime() {
        assert (checkValid());
        String timeString = timeEntry.getText().trim().toLowerCase();

        Pattern secondPattern = Pattern.compile("(?<=^(\\d+\\s*m\\s*)?)\\d+(?=\\s*s?$)");
        Pattern minutePattern = Pattern.compile("^\\d+(?=\\s*m(\\s*\\d+\\s*s?)?$)");
        Matcher secondMatcher = secondPattern.matcher(timeString);
        Matcher minuteMatcher = minutePattern.matcher(timeString);

        long seconds = secondMatcher.find(0) ? Long.parseLong(secondMatcher.group()) : 0;
        long minutes = minuteMatcher.find(0) ? Long.parseLong(minuteMatcher.group()) : 0;
        return ((minutes * 60) + seconds) * 1000;
    }

    // EFFECTS: Returns true if the time input is valid
    @Override
    public boolean checkValid() {
        return timeEntry.checkValid();
    }
}

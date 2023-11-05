package ui.components.routine;

import javax.swing.*;
import java.awt.*;

// Represents a view that displays a message, error, or success
public class InfoDisplay extends JPanel {
    private static final Color MESSAGE_BACKGROUND_COLOUR = new Color(52, 131, 235);
    private static final Color MESSAGE_TEXT_COLOUR = Color.white;
    private static final Color ERROR_BACKGROUND_COLOUR = new Color(209, 63, 50);
    private static final Color ERROR_TEXT_COLOUR = Color.white;
    private static final Color SUCCESS_BACKGROUND_COLOUR = new Color(66, 153, 95);
    private static final Color SUCCESS_TEXT_COLOUR = Color.white;

    private final JLabel label = new JLabel();

    private final Color defaultBackgroundColor;
    private final Color defaultTextColor;

    // EFFECTS: Constructs the info display with nothing presented yet
    public InfoDisplay() {
        super();
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(label);

        label.setFont(new Font("Sans-Serif", Font.BOLD, 13));

        // Keep track of current background and text colours to clear display
        defaultBackgroundColor = getBackground();
        defaultTextColor = label.getForeground();
    }

    // MODIFIES: this
    // EFFECTS: Displays a regular message
    public void displayMessage(String message) {
        display(message, MESSAGE_BACKGROUND_COLOUR, MESSAGE_TEXT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: Displays an error message
    public void displayError(String error) {
        display(error, ERROR_BACKGROUND_COLOUR, ERROR_TEXT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: Displays a success message
    public void displaySuccess(String error) {
        display(error, SUCCESS_BACKGROUND_COLOUR, SUCCESS_TEXT_COLOUR);
    }

    // MODIFIES: this
    // EFFECTS: Clears the display
    public void clear() {
        display("", defaultBackgroundColor, defaultTextColor);
    }

    // MODIFIES: this
    // EFFECTS: Internal helper method to set the info display colours and text
    private void display(String text, Color backgroundColor, Color textColor) {
        setBackground(backgroundColor);
        label.setText(text);
        label.setForeground(textColor);
    }

}

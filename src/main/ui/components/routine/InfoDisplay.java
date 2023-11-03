package ui.components.routine;

import javax.swing.*;
import java.awt.*;

public class InfoDisplay extends JPanel {
    private JLabel label = new JLabel();

    private final Color defaultBackgroundColor;
    private final Color defaultTextColor;

    public InfoDisplay() {
        super();
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(label);

        defaultBackgroundColor = getBackground();
        defaultTextColor = label.getForeground();
    }

    public void displayMessage(String message) {
        display(message, Color.WHITE, Color.BLUE);
    }

    public void displayError(String error) {
        display(error, Color.PINK, Color.RED);
    }

    public void clear() {
        display("", defaultBackgroundColor, defaultTextColor);
    }

    private void display(String text, Color backgroundColor, Color textColor) {
        setBackground(backgroundColor);
        label.setText(text);
        label.setForeground(textColor);
    }

}

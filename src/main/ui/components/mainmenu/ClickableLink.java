package ui.components.mainmenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Represents a link (label) that does some action when clicked
public class ClickableLink extends JLabel {
    public ClickableLink(String label, Runnable callback) {
        super(label);

        // Makes it look like an actual link
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(Color.blue);

        addMouseListener(new MouseAdapter() {
            // EFFECTS: Calls the provided callback function when clicked
            @Override
            public void mouseClicked(MouseEvent e) {
                callback.run();
            }

            // EFFECTS: Sets the text to its default state when no mouse hover
            @Override
            public void mouseExited(MouseEvent e) {
                setText(label);
            }

            // EFFECTS: Sets the text to look like a link (underlined) when mouse hover
            @Override
            public void mouseEntered(MouseEvent e) {
                setText("<html><a href=''>" + label + "</a></html>");
            }
        });
    }
}
package ui.components;

import ui.components.addeditdialog.Updatable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

// Represents a JTextField which can be validated based on given criteria
// Visually shows the validation state of the text using background colours
public class ValidatedTextField extends JTextField implements DocumentListener, Validatable {
    private static final Color VALID_BACKGROUND_COLOR = new Color(186, 245, 202);
    private static final Color INVALID_BACKGROUND_COLOR = new Color(245, 188, 186);

    private final String validationRegex;
    private final Updatable updateCallback;

    // REQUIRES: validationRegex is a valid Regex expression
    // EFFECTS: Constructs a new text field which is validated using the given regex,
    //          calls the given callback on every change of the field, initially set using the
    //          given default value.
    public ValidatedTextField(String validationRegex, Updatable updateCallback, String defaultValue) {
        super();
        this.validationRegex = validationRegex;
        this.updateCallback = updateCallback;
        setText(defaultValue);

        // Attach self as a document listener to track any changes to the text
        getDocument().addDocumentListener(this);
    }

    // EFFECTS: Overrides the maximum size of the text field to be as wide as possible,
    //          but only as high as will fit one line of text
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
    }

    // EFFECTS: Returns true if the text matches the validation regex
    @Override
    public boolean checkValid() {
        return getText().trim().toLowerCase().matches(validationRegex);
    }

    // MODIFIES: this
    // EFFECTS: Updates the background color of the text field and calls the
    //          update callback to say that a change was made.
    private void update() {
        if (checkValid()) {
            setBackground(VALID_BACKGROUND_COLOR);
        } else {
            setBackground(INVALID_BACKGROUND_COLOR);
        }

        updateCallback.update();
    }

    // MODIFIES: this
    // EFFECTS: Updates the text field if a character was added
    @Override
    public void insertUpdate(DocumentEvent e) {
        update();
    }

    // MODIFIES: this
    // EFFECTS: Updates the text field if a character was removed
    @Override
    public void removeUpdate(DocumentEvent e) {
        update();
    }

    // MODIFIES: this
    // EFFECTS: Updates the text field if a character was changed
    @Override
    public void changedUpdate(DocumentEvent e) {
        update();
    }
}

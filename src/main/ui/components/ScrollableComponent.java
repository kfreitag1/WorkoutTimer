package ui.components;

import javax.swing.*;

// Represents a generic scrollable component, scrolls in the vertical direction and only
// shows the scrollbar if needed
public abstract class ScrollableComponent extends JScrollPane {
    protected final JPanel body;

    // EFFECTS: Constructs a new scrollable component
    public ScrollableComponent() {
        super(new JPanel(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        body = (JPanel) getViewport().getView();

        // Fix scrolling to not be so slow
        getVerticalScrollBar().setUnitIncrement(3);
    }
}

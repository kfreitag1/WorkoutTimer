package ui.components.addeditdialog;

import model.Segment;

// Represents a callback object which can receive a segment
public interface SegmentReceiver {
    void receiveSegment(Segment segment);
}

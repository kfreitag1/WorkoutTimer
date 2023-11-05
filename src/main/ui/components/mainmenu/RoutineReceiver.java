package ui.components.mainmenu;

import model.Routine;

// Represents an object which can receive a routine
public interface RoutineReceiver {
    void receiveRoutine(Routine routine);
}

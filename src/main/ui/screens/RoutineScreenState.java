package ui.screens;

// Represents the possible states of the routine screen,
// can only be one of these at a time
public enum RoutineScreenState {
    DEFAULT,
    RUNNING,
    EDITING,
    ADDING,
    DELETING;

    // EFFECTS: Returns true if the current state is a "selecting state",
    //          i.e. one where the user is selecting something:
    //          (editing, adding, or deleting)
    public boolean isSelectingState() {
        return (this == EDITING || this == ADDING || this == DELETING);
    }
}

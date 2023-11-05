<div align="center">
  <h1>WorkoutTimer</h1>
</div>

**WorkoutTimer** is a Java desktop application to create custom workout timer routines. 
Each timer routine consists of a series of timed or untimed segments (i.e. press to continue).
Groups of segments can also be repeated.

*Example Timer Routine:*
- **5 min** – Sprints
- **1.5 min** – Rest
- Repeat **10x**:
   - **1 min** – Burpees
   - **8 sec** – Rest
   - **Untimed** – 10 Squats
- **2 min** – Jumprope

Primarily for use by anyone with an interval-based workout routine (E.g. bodyweight exercises, yoga, etc.). 

**But it's not just for workouts!** Can be used in any other situation with 
defined procedures of timed/untimed segments. 
*For 
example:*
1. Studying / productivity time management 
(e.g. [Pomodoro technique](https://en.wikipedia.org/wiki/Pomodoro_Technique), 
[52/17 Rule](https://en.wikipedia.org/wiki/52/17_rule))
2. Interview practice (e.g. MMI timing)
3. Lab protocols
4. Cooking / baking


## User stories

- As a user, I want to be able to add multiple timer segments (either 1. a timed segment, 2. a
  manually activated segment, or 3. a group of segments to repeat a certain number of times) to any arbitrary 
  location in my routine
- As a user, I want to be able to see all the timer segments in my routine
- As a user, I want to be able to delete any arbitrary segment in my routine
- As a user, I want to be able to edit the parameters (e.g. name, time, repetitions, etc.) of any arbitrary segment 
  in my routine
- As a user, I want to be able to play, pause, and restart execution of my routine
- As a user, I want to be able to see the current status of each timer segment when my routine is running

- As a user, I want to have the option to save the entire state of my routine to a file (also have the option to save 
  when close routine)
- As a user, I want to have the option to load any saved routine resume exactly where I was


## Instructions for Grader

1. Load a pre-existing routine by clicking on it in the main menu, or create a new empty routine by entering a 
   routine name and then clicking on "Create new routine".
2. You can generate the first required action related to adding "Segments" to a "Routine" by clicking on the "Add" 
   button, entering information about the segment into the dialog popup, and then once completed, clicking on the 
   location in which to insert the new segment (will place by default if the routine is empty).
3. You can generate the second required action related to deleting/editing "Segments" on a "Routine" by clicking on 
   the respective "Delete" or "Edit" buttons and following the onscreen instructions as with 2.
4. You can locate my visual component by playing the routine (press the button with the play symbol). Time and 
   repeat segments include a custom progress bar that changes size/colour depending on their completion states.
5. You can save the application by pressing "Save", or by pressing "Close" and then pressing "Yes" on the dialog box 
   asking to save.
6. You can reload the application by pressing on the name of the application on the Main Menu screen.

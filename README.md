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


## Phase 4: Task 2

Representative event log:
- Mon Nov 27 13:26:29 PST 2023

  Added a segment with name: Push-ups

- Mon Nov 27 13:26:53 PST 2023

  Inserted new segment with name: Pull-ups 10x, after segment: Push-ups

- Mon Nov 27 13:26:59 PST 2023

  Removed segment with name: Pull-ups 10x

- Mon Nov 27 13:27:07 PST 2023

  Inserted new segment with name: Plank, before segment: Push-ups

- Mon Nov 27 13:27:13 PST 2023

  Removed segment with name: Push-ups

## Phase 4: Task 3

Overall, although the UML diagram looks fairly complicated, I believe that it represents a fairly simple code 
structure. Most of the complexity is in the ui package, which is inherent for complex UI-based applications, and 
even so the components are fairly modular. I tried to design the classes so that they are cohesive and only modify 
other classes through defined callback functions.

Completed Refactoring:
- Added in enum classes where I needed a data type that represents one of a discrete number of options. Previously I 
  would use strings to store this data, but enums are more accurate and less prone to making errors (e.g. need to 
  remember the exact string used for each option). I often used switch statements to check the value of each of 
  these data types and would need to include a 'default' option since strings could store values other than the 
  discrete number of options I needed. I thought that when I switched over to enums I would be able to remove this 
  'default' options since it should be a compiler error for an enum to hold a value other than one of the defined 
  ones... but it seems that in Java this is not the case, you still need the default case when it would be 
  impossible for it to occur.
  - Added SegmentType to represent the types of the segments (MANUAL, TIME, REPEAT, ROUTINE)
  - Added RoutineScreenState to represent the possible states of the routine screen (DEFAULT, RUNNING, EDITING, ADDING, DELETING)
  - Added PlayPauseRewindIcon.Type to represent the possible icon types (PLAY, PAUSE, REWIND)
  - Added SegmentDisplay.State to represent the possible states of the segment display (DEFAULT, CURRENT, COMPLETE)
  - Added RoutineJsonJey to represent the possible JSON keys for different data types
- The 'Segment' classes and Routine were very similar to the composite pattern since RepeatSegment and Routine 
  objects could store lists of segments (which could also be RepeatSegments). I refactored this to be much more 
  explicit by making Segment and SegmentGroup abstract classes that represent the component and composite classes in 
  the composite pattern, respectively. Routine and RepeatSegment now extend from SegmentGroup (the composite). 
  ManualSegment and TimeSegment continue to extend from Segment (they act as leaf classes). This allowed me to 
  remove a lot of code duplication.

Refactoring TODO:
- There are a lot of constants throughout the application, so it might be nice to put these in one single location. 
  Could make a singleton, or a static class to store these values.
- I chose to make use of 'requires' clauses instead of exceptions in most cases where the input to a function needs 
  to be of a certain state. I believe this is the best approach for this application since the code is only executed 
  from other internal functions (i.e. not creating an external library). However, it might be beneficial to add in 
  assert statements throughout to verify that these 'requires' classes are being met during further development.




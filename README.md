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

- As a user, I want to be able to create a new timer routine with an 
arbitrary number of timed, untimed, or repeated segments.
- As a user, I want to be able to view all my saved timer routines.
- As a user, I want to be able to modify the segments within an opened timer routine.
- As a user, I want to be able to save and load timer routines.
- As a user, I want to be able to play, pause, and restart the currently opened timer routine.
- As a user, I want to be able to select a segment within a timer routine and modify its parameters (i.e. 
  time or untimed, label, countdown alarm (e.g. beeps for 3 sec, 2 sec, 1 sec))
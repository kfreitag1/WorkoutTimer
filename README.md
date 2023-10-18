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

- As a user, I want to be able to add a timer segment to the end of my routine (either 1. a timed segment, 2. a 
  manually activated 
  segment, or 3. a group of segments to repeat a certain number of times)
- As a user, I want to be able to see all the timer segments in my routine
- As a user, I want to be able to delete any arbitrary segment in my routine
- As a user, I want to be able to insert a new segment before/after any other arbitrary segment in my routine
- As a user, I want to be able to edit the parameters (e.g. name, time, repetitions, etc.) of any arbitrary segment 
  in my routine
- As a user, I want to be able to play, pause, and restart execution of my routine
- As a user, I want to be able to see the current status of each timer segment when my routine is running

- As a user, I want to have the option to save the entire state of my routine to a file (also have the option to save 
  when close routine)
- As a user, I want to have the option to load my routine from the saved file and start off exactly where I was
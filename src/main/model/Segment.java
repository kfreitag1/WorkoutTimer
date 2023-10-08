package model;

// a timer segment that can be one of three states:
// 1. runs for a certain time
// 2. a list of timer segments to repeat x times, can have arbitrary number of repetition groups udner it
// 3. requires manual activation

public interface Segment {
    public String getName();

    public String getType();

    public boolean isComplete();

    public void reset();

    public void setName(String newName);
}

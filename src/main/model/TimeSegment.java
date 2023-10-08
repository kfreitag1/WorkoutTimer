package model;

import static java.lang.Math.min;

public class TimeSegment implements Segment {
    private String name;
    private long totalTime;
    private long currentTime;
    private final Segment parent;
    private final int layer;

    // parent can be null if root routine
    public TimeSegment(String name, long totalTime, Segment parent) {
        this.name = name;
        this.totalTime = totalTime;
        this.parent = parent;

        layer = parent == null ? 0 : parent.getLayer() + 1;
        currentTime = 0;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    // returns unused milliseconds if the segment is complete, otherwise 0
    public long addTime(long milliseconds) {
        long newTime = currentTime + milliseconds;
        currentTime = min(totalTime, newTime);
        return newTime - currentTime;
    }

    // requires newTotalTime > 0, currentTime = 0 (not running)
    public void setTotalTime(long newTotalTime) {
        totalTime = newTotalTime;
    }

    // Segment methods
    // --------------------------------------------------------------------------------------------

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "time";
    }

    @Override
    public boolean isComplete() {
        return currentTime >= totalTime;
    }

    @Override
    public void reset() {
        currentTime = 0;
    }

    @Override
    public void setName(String newName) {
        this.name = newName;
    }

    @Override
    public Segment getParent() {
        return this.parent;
    }

    @Override
    public int getLayer() {
        return this.layer;
    }
}

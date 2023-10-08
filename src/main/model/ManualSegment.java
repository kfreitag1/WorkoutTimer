package model;

public class ManualSegment implements Segment {
    private String name;
    private boolean finished = false;

    public ManualSegment(String name) {
        this.name = name;
    }

    public void setComplete() {
        finished = true;
    }

    // Segment methods
    // --------------------------------------------------------------------------------------------

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "manual";
    }

    @Override
    public boolean isComplete() {
        return finished;
    }

    @Override
    public void reset() {
        finished = false;
    }

    @Override
    public void setName(String newName) {
        name = newName;
    }
}

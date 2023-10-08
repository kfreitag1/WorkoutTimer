package model;

public class ManualSegment implements Segment {
    private String name;
    private boolean finished = false;
    private final Segment parent;
    private final int layer;

    // parent is null if root routine
    public ManualSegment(String name, Segment parent) {
        this.name = name;
        this.parent = parent;

        layer = parent == null ? 0 : parent.getLayer() + 1;

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

    @Override
    public Segment getParent() {
        return parent;
    }

    @Override
    public int getLayer() {
        return layer;
    }
}

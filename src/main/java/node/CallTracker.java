package node;

public class CallTracker {
    private int caller;
    private int steps;

    public CallTracker(int caller, int steps) {
        this.caller = caller;
        this.steps = steps;
    }

    public int getCaller() {
        return caller;
    }

    public void setCaller(int caller) {
        this.caller = caller;
    }

    public int getSteps() {
        return steps;
    }

    public void addStep(){
        this.steps += 1;
    }
}

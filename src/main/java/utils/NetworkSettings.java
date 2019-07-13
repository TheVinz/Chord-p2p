package utils;

import java.util.Random;

public class NetworkSettings {

    private boolean delay=false;
    private long maxDelay=500;
    private long requestTimeout = 5000;
    private long stabilizeDelay = 500;
    private long stabilizePeriod = 250;
    private long fixFingerDelay = 800;
    private long fixFingerPeriod = 250;
    private long checkPredecessorDelay = 1000;
    private long checkPredecessorPeriod = 250;
    private long checkSuccessorDelay = 1000;
    private long checkSuccessorPeriod = 250;


    public boolean isDelay() {
        return delay;
    }

    public long getDelay(){
            return (new Random()).nextLong() % maxDelay;
    }

    public long getRequestTimeout(){
        return requestTimeout;
    }

    public long getStabilizeDelay() {
        return stabilizeDelay;
    }

    public long getStabilizePeriod() {
        return stabilizePeriod;
    }


    public long getFixFingerDelay() {
        return fixFingerDelay;
    }

    public long getFixFingerPeriod() {
        return fixFingerPeriod;
    }

    public long getCheckPredecessorDelay() {
        return checkPredecessorDelay;
    }

    public long getCheckPredecessorPeriod() {
        return checkPredecessorPeriod;
    }

    public long getCheckSuccessorDelay() {
        return checkSuccessorDelay;
    }

    public long getCheckSuccessorPeriod() {
        return checkSuccessorPeriod;
    }

    public long[] getRoutineDelays() {
        return new long[] {
                getStabilizeDelay(),
                getFixFingerDelay(),
                getCheckPredecessorDelay(),
                getCheckSuccessorDelay()
        };
    }

    public long[] getRoutinePeriods() {
        return new long[] {
                getStabilizePeriod(),
                getFixFingerPeriod(),
                getCheckPredecessorPeriod(),
                getCheckSuccessorPeriod()
        };
    }
}

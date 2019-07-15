package utils;

import java.util.Random;
import java.util.logging.Level;

public class NetworkSettings {

    private boolean delay=false;
    private int maxDelay=500;
    private long requestTimeout = 5000;
    private long stabilizeDelay = 500;
    private long stabilizePeriod = 1000;
    private long fixFingerDelay = 800;
    private long fixFingerPeriod = 1000;
    private long checkPredecessorDelay = 1000;
    private long checkPredecessorPeriod = 500;
    private long checkSuccessorDelay = 1000;
    private long checkSuccessorPeriod = 500;
    private LoggingLevel loggingLevel = LoggingLevel.FINE;


    public boolean isDelay() {
        return delay;
    }

    public long getDelay(){
            return (new Random()).nextInt(maxDelay);
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

    public Level getLoggingLevel() {
        try {
            return Level.parse(loggingLevel.name());
        } catch (NullPointerException npe) {
            return Level.FINE;
        }
    }

    private enum LoggingLevel {
        OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL,
    }
}

package utils;

import java.util.Random;

public class NetworkSettings {
    boolean delay=false;
    long maxDelay=10;

    public boolean isDelay() {
        return delay;
    }

    public long getDelay(){
            return (new Random()).nextLong()*maxDelay;
    }
}

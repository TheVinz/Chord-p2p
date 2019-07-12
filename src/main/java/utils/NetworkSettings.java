package utils;

import java.util.Random;

public class NetworkSettings {

    private boolean delay=false;
    private long maxDelay=500;
    private long requestTimeout = 5000;


    public boolean isDelay() {
        return delay;
    }

    public long getDelay(){
            return (new Random()).nextLong() % maxDelay;
    }

    public long getRequestTimeout(){
        return requestTimeout;
    }
}

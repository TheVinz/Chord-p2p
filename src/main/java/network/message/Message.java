package network.message;

import java.io.Serializable;

public abstract class Message implements Serializable {
    public static final long serialVersionUID = 562056L;

    private int requestId;

    public void setRequestId(int requestId){
        this.requestId=requestId;
    }

    public int getRequestId() {
        return requestId;
    }
}

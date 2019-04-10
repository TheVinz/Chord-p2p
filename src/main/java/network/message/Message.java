package network.message;

import java.io.Serializable;

public abstract class Message implements Serializable {
    public static final long serialVersionUID = 562056L;
    public static final int FIND_SUCCESSOR = 0;
    public static final int GET_PREDECESSOR = 1;
    public static final int GET_SUCCESSOR = 2;
    public static final int NOTIFY_PREDECESSOR = 3;

    public final int method;
    public final String ip;
    public final int port;
    public final int id;
    private int requestId;

    Message(){
        method=0;
        ip=null;
        port=0;
        id=0;
    }

    Message(int method){
        this.method=method;
        this.id=0;
        this.ip=null;
        this.port=0;
    }

    Message(int method, int id){
        this.method=method;
        this.id=id;
        this.ip=null;
        this.port=0;
    }

    Message(int method, String ip, int port, int id){
        this.method=method;
        this.id=id;
        this.ip=ip;
        this.port=port;
    }

    public void setRequestId(int requestId){
        this.requestId=requestId;
    }

    public int getRequestId() {
        return requestId;
    }
}

package network.remoteNode;

import network.exceptions.NetworkFailureException;
import network.message.request.RequestMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;

class OutputBuffer {

    private final ObjectOutputStream oos;
    private boolean closed=false;
    private int currentRequestId=0;


    OutputBuffer(ObjectOutputStream oos) throws IOException {
        this.oos=oos;
    }

    synchronized Request sendRequest(RequestMessage msg) throws NetworkFailureException {
        int requestId;
        requestId=currentRequestId;
        currentRequestId++;
        msg.setRequestId(requestId);
        try {
            oos.writeObject(msg);
        } catch (IOException e) {
            close();
            throw new NetworkFailureException();
        }
        return new Request(requestId);
    }

    boolean isClosed() {
        return closed;
    }

    public void close() {
        closed=true;
    }
}

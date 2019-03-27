package network.remoteNode;

import network.exeptions.NetworkFailureException;
import network.message.RequestMessage;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

class OutputBuffer implements Closeable {

    private final ObjectOutputStream oos;
    private boolean closed=false;
    private int currentRequestId=0;


    OutputBuffer(Socket socket) throws IOException {
        oos= new ObjectOutputStream(socket.getOutputStream());
    }

    Request sendRequest(RequestMessage msg) throws NetworkFailureException {
        int requestId;
        synchronized (this){
            requestId=currentRequestId;
            currentRequestId++;
        }
        msg.setRequestId(requestId);
        try {
            oos.writeObject(msg);
        } catch (IOException e) {
            throw new NetworkFailureException();
        }
        return new Request(requestId);
    }

    boolean isClosed() {
        return closed;
    }

    public void close() throws IOException {
        closed=true;
    }
}

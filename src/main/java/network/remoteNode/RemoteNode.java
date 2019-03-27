package network.remoteNode;

import network.exeptions.NetworkFailureException;
import network.message.Message;
import network.message.ReplyMessage;
import network.message.RequestMessage;
import node.CallTracker;
import node.Node;
import node.Notifier;
import node.exceptions.NodeNotFoundException;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;


/**
 * This class is the abstraction of a node that lives in a remote machine.
 * So each method will resolve remotely on the other machine and eventually returns to the local client.
 */
public class RemoteNode implements Notifier, Closeable {

    private final int id;
    private final String ip;
    private final int port;
    private Socket socket;
    private InputBuffer inputBuffer;
    private OutputBuffer outputBuffer;
    private PendingRequestQueue queue= new PendingRequestQueue();
    private boolean closed=true;


    public RemoteNode(int id, String ip, int port) {
        this.id=id;
        this.ip=ip;
        this.port=port;
    }

    public void setUpConnection() throws NetworkFailureException {
        try {
            this.socket=new Socket(ip, port);
            outputBuffer=new OutputBuffer(socket);
            inputBuffer=new InputBuffer(socket, queue);
            closed=false;
        } catch (IOException e) {
            throw new NetworkFailureException();
        }
    }

    public Node findSuccessor(int id, CallTracker trackes) throws NodeNotFoundException, NetworkFailureException {
        if(closed)
            throw new NetworkFailureException("Connection with the remote node currently closed");
        RequestMessage msg = new RequestMessage(Message.FIND_SUCCESSOR, id);
        Request request = outputBuffer.sendRequest(msg);
        ReplyMessage reply = queue.submitRequest(request);
        if(reply.ip==null)
            throw new NodeNotFoundException();
        else
            return new RemoteNode(reply.id, reply.ip, reply.port);

    }

    public Node getPredecessor() throws NodeNotFoundException, NetworkFailureException {
        if(closed)
            throw new NetworkFailureException("Connection with the remote node currently closed");
        RequestMessage msg = new RequestMessage(Message.GET_PREDECESSOR);
        Request request = outputBuffer.sendRequest(msg);
        ReplyMessage reply = queue.submitRequest(request);
        if(reply.ip==null)
            throw new NodeNotFoundException();
        else
            return new RemoteNode(reply.id, reply.ip, reply.port);
    }

    public Node getSuccessor() throws NetworkFailureException {
        if(closed)
            throw new NetworkFailureException("Connection with the remote node currently closed");
        RequestMessage msg = new RequestMessage(Message.GET_SUCCESSOR);
        Request request = outputBuffer.sendRequest(msg);
        ReplyMessage reply = queue.submitRequest(request);
        if(reply.ip==null)
            return null;
        else
            return new RemoteNode(reply.id, reply.ip, reply.port);
    }

    public void notifyPredecessor(Node n) throws NetworkFailureException {
        if(closed)
            throw new NetworkFailureException("Connection with the remote node currently closed");
        RequestMessage msg = new RequestMessage(Message.NOTIFY_PREDECESSOR, n.getIp(), n.getPort(), n.getId());
        outputBuffer.sendRequest(msg);
    }

    public boolean isClosed(){
        return closed;
    }

    public int getId() {
        return id;
    }

    @Override
    public int getPort(){
        return port;
    }

    @Override
    public String getIp() {
        return ip;
    }

    public void close() throws IOException {
        inputBuffer.close();
        outputBuffer.close();
        socket.close();
        closed=true;
    }
}

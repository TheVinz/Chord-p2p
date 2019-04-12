package network.remoteNode;

import network.exeptions.NetworkFailureException;
import network.message.reply.NodeReply;
import network.message.reply.ReplyMessage;
import network.message.reply.ResourceReply;
import network.message.request.*;
import resource.ChordResource;
import node.Node;
import node.exceptions.NodeNotFoundException;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * This class is the abstraction of a node that lives in a remote machine.
 * So each method will resolve remotely on the other machine and eventually returns to the local client.
 */
public class RemoteNode implements Node, Closeable {

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
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            outputBuffer=new OutputBuffer(oos);
            inputBuffer= new InputBuffer(ois, queue);
            closed=false;
        } catch (IOException e) {
            throw new NetworkFailureException();
        }
    }

    @Override
    public Node findSuccessor(int id) throws NetworkFailureException {
        try {
            if (closed)
                setUpConnection();
            Request request = outputBuffer.sendRequest(new FindSuccessorRequest(id));
            NodeReply reply = (NodeReply) queue.submitRequest(request);

            return new RemoteNode(reply.getId(), reply.getIp(), reply.getPort());
        }
        catch (NetworkFailureException e){
            e.setMessage("Failed to contact Node " + this.id + " on findSuccessor");
            throw e;
        }
    }

    @Override
    public Node getPredecessor() throws NodeNotFoundException, NetworkFailureException {
        try {
            if (closed)
                setUpConnection();
            RequestMessage msg = new GetPredecessorRequest();
            Request request = outputBuffer.sendRequest(msg);
            NodeReply reply = (NodeReply) queue.submitRequest(request);
            return new RemoteNode(reply.getId(), reply.getIp(), reply.getPort());
        } catch (NetworkFailureException e){
            e.setMessage("Failed to contact Node " + id + " on getPredecessor");
            throw e;
        }
    }

    @Override
    public Node getSuccessor() throws NetworkFailureException {
        if(closed)
            setUpConnection();
        RequestMessage msg = new GetSuccessorRequest();
        Request request = outputBuffer.sendRequest(msg);
        NodeReply reply = (NodeReply) queue.submitRequest(request);
        return new RemoteNode(reply.getId(), reply.getIp(), reply.getPort());
    }

    @Override
    public void notifyPredecessor(Node n) throws NetworkFailureException {
        if(closed)
            setUpConnection();
        RequestMessage msg = new NotifyPredecessorRequest(n);
        outputBuffer.sendRequest(msg);
    }

    @Override
    public void publish(ChordResource resource) throws NetworkFailureException {
        if(closed)
            setUpConnection();
        PublishRequest msg = new PublishRequest(resource);
        outputBuffer.sendRequest(msg);
    }

    @Override
    public ChordResource fetch(String name) throws NetworkFailureException {
        if(closed)
            setUpConnection();
        FetchMessage msg = new FetchMessage(name);
        Request request = outputBuffer.sendRequest(msg);
        ReplyMessage reply = queue.submitRequest(request);
        if(reply instanceof ResourceReply)
            return ((ResourceReply) reply).getResource();
        else return null;
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

    @Override
    public void close() throws IOException {
        if(!closed) {
            inputBuffer.close();
            outputBuffer.close();
            socket.close();
            closed = true;
        }
    }
}

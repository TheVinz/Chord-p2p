package network.remoteNode;

import network.exceptions.NetworkFailureException;
import network.message.reply.NodeReply;
import network.message.reply.ReplyMessage;
import network.message.reply.ResourceReply;
import network.message.reply.SuccessorListReply;
import network.message.request.*;
import node.Node;
import resource.ChordResource;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is the abstraction of a node that lives in a remote machine.
 * So each method will resolve remotely on the other machine and eventually returns to the local client.
 */
public class RemoteNode implements Node {

    private final int id;
    private final String ip;
    private final int port;
    private Socket socket;
    private OutputBuffer outputBuffer;
    private InputBuffer inputBuffer;
    private PendingRequestQueue queue;
    private boolean closed=true;


    public RemoteNode(int id, String ip, int port) {
        this.id=id;
        this.ip=ip;
        this.port=port;
    }

    private void setUpConnection() throws NetworkFailureException {
        try {
            this.queue=new PendingRequestQueue();
            this.socket=new Socket(ip, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            outputBuffer=new OutputBuffer(oos);
            inputBuffer = new InputBuffer(ois, queue, this.id);
            closed = false;
        } catch (IOException e) {
            // TODO: should manage close?
            close();
            throw new NetworkFailureException(e);
        }
    }

    @Override
    public Node findSuccessor(int id) throws NetworkFailureException {
        try {
            if (closed)
                setUpConnection();
            Request request = outputBuffer.sendRequest(new FindSuccessorRequest(id));
            NodeReply reply = (NodeReply) queue.submitRequest(request);

            if(reply.isNotFound())
                return null;
            else
                return new RemoteNode(reply.getId(), reply.getIp(), reply.getPort());
        }
        catch (NetworkFailureException e){
            e.setMessage("Failed to contact Node " + this.id + " on findSuccessor");
            throw e;
        }
    }

    @Override
    public Node getPredecessor() throws NetworkFailureException {
        try {
            if (closed)
                setUpConnection();
            RequestMessage msg = new GetPredecessorRequest();
            Request request = outputBuffer.sendRequest(msg);
            NodeReply reply = (NodeReply) queue.submitRequest(request);
            if(reply.isNotFound())
                return null;
            else
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
    public List<Node> getSuccessorsList() throws NetworkFailureException {
        if(closed)
            setUpConnection();
        RequestMessage msg = new SuccessorListRequest();
        Request request = outputBuffer.sendRequest(msg);
        SuccessorListReply reply = (SuccessorListReply) queue.submitRequest(request);
        List<Node> successorsList = new ArrayList<>();
        for(int i=0; i<reply.getIds().size(); i++){
            Node remoteNode = new RemoteNode(reply.getIds().get(i), reply.getIps().get(i), reply.getPorts().get(i));
            successorsList.add(remoteNode);
        }
        return successorsList;
    }


    public void publish(ChordResource resource) throws NetworkFailureException {
        if(closed)
            setUpConnection();
        PublishRequest msg = new PublishRequest(resource);
        outputBuffer.sendRequest(msg);
    }

    @Override
    public boolean hasFailed() throws NetworkFailureException{
        if(closed)
            setUpConnection();
        RequestMessage msg = new PingRequest();
        Request request = outputBuffer.sendRequest(msg);
        queue.submitRequest(request);
        if(request.isFailed())
            throw new NetworkFailureException(); // TODO: I would just return true, why thr?
        else
            return false;
    }

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

    /**
     * Close sockets and communication devices.
     * The node cannot communicate anymore.
     */
    @Override
    public void close() {
        try { // TODO the opposite try -- if
            if (!closed) {
                inputBuffer.close();
                outputBuffer.close();
                socket.close();
                queue.close();
            }
        } catch (IOException e){
            // TODO understand logging or re-throwing. What happen to the remaining unclosed?
            e.printStackTrace();
        } finally {
            closed = true;
        }
    }

    /**
     * Making a copy of this node with only the final attributes.
     * @return a copy of this node with same id, ip and port
     */
    @Override
    public Node wrap() {
        return new RemoteNode(getId(), getIp(), getPort());
    }
}

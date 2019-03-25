package node;

import node.exceptions.NodeNotFoundException;


/**
 * This class is the abstraction of a node that lives in a remote machine.
 * So each method will resolve remotely on the other machine and eventually returns to the local client.
 */
public class RemoteNode implements Notifier{

    private int id;

    protected RemoteNode(int id) {

        this.id = id;
    }

    public Node findSuccessor(int id, CallTracker callTracker) throws NodeNotFoundException {
        return null;
    }

    public Node getPredecessor() throws NodeNotFoundException {
        return null;
    }

    public Node getSuccessor() {
        return null;
    }

    public int getId() {
        return id;
    }

    public void notifyPredecessor(Node n) {

    }
}

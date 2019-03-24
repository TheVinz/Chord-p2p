package node;

import node.exceptions.NodeNotFoundException;

public class RemoteNode implements Notifier{

    private int id;

    protected RemoteNode(int id) {

        this.id = id;
    }

    public Node findSuccessor(int id) throws NodeNotFoundException {
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

package node;

public interface Node {


    Node findSuccessor(int id) throws NodeNotFoundException;
    Node getPredecessor() throws NodeNotFoundException;
    Node getSuccessor();
    int getId();
}

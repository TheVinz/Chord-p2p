package node;

import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;

public interface Node {


    Node findSuccessor(int id, CallTracker callTracker) throws NodeNotFoundException, FingerTableEmptyException;
    Node getPredecessor() throws NodeNotFoundException;
    Node getSuccessor();
    int getId();
}

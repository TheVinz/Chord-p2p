package node;

import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;

/**
 * Main abstraction of the concept of node as part of a Chord ring.
 * This description models the fact that nodes might be not available, as well as
 * the finger table not completely initialised yet.
 */
public interface Node {


    /**
     * It finds the node whose id is immediate succeeding the resource {@code id} in the chord ring.
     * @param id the identifier of the resource to find (a node or a key)
     * @param callTracker some information to track the initial callee.
     *                    This information just need to be forwarded each call.
     *                    At the same time is used to check whether there is a cycle in the calls.
     * @return the reference of the node succeeding id.
     * @throws NodeNotFoundException when this node is not available to be contacted.
     *                               Alternatively, when there is a cycle and the initial callee is encountered.
     * @throws FingerTableEmptyException when the finger table entry that captures the param id is not initialised yet.
     */
    Node findSuccessor(int id, CallTracker callTracker) throws NodeNotFoundException, FingerTableEmptyException;

    /**
     * Gets the node whose id is smaller then the current node.
     * The result might not be always the true predecessor.
     * For instance this happens when a new node joins the ring between the current node and its predecessor.
     * Therefore this node cannot know it, until is not contacted.
     * @return The reference of the node that precedes this one.
     * @throws NodeNotFoundException when this node is not available to be contacted.
     */
    Node getPredecessor() throws NodeNotFoundException;

    /**
     * Gets the immediate successor node in the ring.
     * @return the successor reference.
     */
    Node getSuccessor(); // TODO should throw NodeNotFoundException as well, since this node might be not available

    /**
     * Gets the identifier of this node.
     * @return this node's identifier.
     */
    int getId();
}

package node;

import network.exceptions.NetworkFailureException;
import resource.ChordResource;

import java.util.List;

/**
 * Main abstraction of the concept of node as part of a Chord ring.
 * This description models the fact that nodes might be not available, as well as
 * the finger table not completely initialised yet.
 * So every time this node is not available, this might be modeled using the {@link NetworkFailureException}
 */
public interface Node {

    /**
     * It finds the node whose id is immediate succeeding the resource {@code id} in the chord ring.
     * @param id the identifier of the resource to find (a node or a key)
     * @return the reference of the node succeeding id.
     * @throws NetworkFailureException when this node is not available to be contacted.
     *                               Alternatively, when there is a cycle and the initial callee is encountered.
     */
    Node findSuccessor(int id) throws NetworkFailureException;

    /**
     * Gets the node whose id is smaller then the current node.
     * The result might not be always the true predecessor.
     * For instance this happens when a new node joins the ring between the current node and its predecessor.
     * Therefore this node cannot know it, until is not contacted.
     * @return The reference of the node that precedes this one.
     */
    Node getPredecessor() throws NetworkFailureException;

    /**
     * Gets the immediate successor node in the ring.
     * @return the successor reference.
     */
    Node getSuccessor() throws NetworkFailureException;

    /**
     * Passive semantics: node n might be the predecessor of this node.
     * If so, set it in place of the current one.
     * @param n the node pretending to be this node's predecessor.
     * @throws NetworkFailureException when this node is not available to answer.
     */
    void notifyPredecessor(Node n) throws NetworkFailureException;

    List<Node> getSuccessorsList() throws NetworkFailureException;

    void publish(ChordResource resource) throws NetworkFailureException;

    ChordResource fetch(String name) throws NetworkFailureException;


    /**
     * Verifies whether this node is available
     * @return true if the node is not available
     */
    boolean hasFailed() throws NetworkFailureException;

    /**
     * Gets the identifier of this node.
     * @return this node's identifier.
     */
    int getId();

    /**
     * Gets the port of the node
     * @return the node's socket port
     */
    int getPort();

    /**
     * Gets the ip address of the node
     * @return the node's IP address
     */
    String getIp();

    /**
     * Closes the node communication.
     * The node might get unavailable, check concrete implementation of this method
     */
    void close();

    /**
     * Wraps the current node. The node returned is semantically a copy
     * such that it's safer to be returned externally of Node rather than <pre>this</pre>.
     * @return the copy of this node
     */
    Node wrap();

    Boolean notifyPropagation(String title) throws NetworkFailureException;

    void notifyDelete(String title) throws NetworkFailureException;

    void sendReplica(ChordResource chordResource) throws NetworkFailureException;
}

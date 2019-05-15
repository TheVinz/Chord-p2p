package node;

import network.exceptions.NetworkFailureException;
import utils.Util;

import java.util.function.Consumer;

/**
 * This node implementation is able to simulate failures, that are useful for testing purpose.
 *
 * So far we are assuming that if a node fails, then it cannot contact/be contacted with/from anyone.
 */
public class FailingNode extends StabilizerNode {
    private boolean hasFailed;

    /**
     * Constructs a Node by specifying its identifier id and initialising its finger table entries to <pre>null</pre>.
     * However this constructor is still populating the keys ({@link FingerTableEntry#setStart(int)}
     * of the entries in the fingerTable, computing them from the entry's index and {@link Util#M}
     *
     * @param id the identifier for this node.
     *           This identifier shall be generated by a consistent hash function, computed on a singular information,
     *           as IP:PORT
     */
    public FailingNode(int id, Consumer<LocalNode>[] tasks, String[] labels, long[] delays, long[] periods) {
        super(id, tasks, labels, delays, periods);
        hasFailed = false;
    }

    public FailingNode(int id, Node node, Consumer<LocalNode>[] tasks, String[] labels, long[] delays, long[] periods) throws  NetworkFailureException {
        super(id, node, tasks, labels, delays, periods);
        hasFailed = false;
    }

    /**
     * Sets whether the node has failed.
     * @param hasFailed true if you want the node to be not available
     */
    public void setHasFailed(boolean hasFailed) {

        this.hasFailed = hasFailed;
        if(hasFailed)
            super.close();
    }

    /*
     * Node's methods overrides
     */

    @Override
    public Node findSuccessor(int id) throws NetworkFailureException {
        if(!hasFailed)
            return super.findSuccessor(id);
        throw new NetworkFailureException();
    }

    @Override
    public Node getSuccessor() throws NetworkFailureException {
        if(!hasFailed)
            return super.getSuccessor();
        throw new NetworkFailureException();
    }

    @Override
    public Node getPredecessor() throws NetworkFailureException {
        if(!hasFailed)
            return super.getPredecessor();
        throw new NetworkFailureException();
    }

    @Override
    public void notifyPredecessor(Node n) throws NetworkFailureException {
        if(!hasFailed)
            super.notifyPredecessor(n);
        else
            throw new NetworkFailureException();
    }

    /**
     * A local node never fails
     * @return false always
     */
    @Override
    public boolean hasFailed() throws NetworkFailureException {
        if(hasFailed)
            throw new NetworkFailureException();
        return hasFailed;
    }
}

package node;

import distributedDB.ResourceManager;
import network.exceptions.NetworkFailureException;
import network.remoteNode.RemoteNode;
import resource.ChordResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static utils.Util.*;

/**
 * Abstract implementation of the node on the local machine.
 * The local node is fully operative either by creating a ring (initially including only itself),
 * or by joining an existing node, The existing node can live on the same machine or remotely.
 */
public class LocalNode implements Node{

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 8888;
    private FingerTable fingerTable;
    private Node predecessor = this;
    private final Object predecessor_lock = new Object();
    private int next = 0;
    private int id;
    private final String host;
    private final int port;
    private List<Node> successorsList;
    private ResourceManager resourceManager;

    /**
     * Constructs a Node by specifying its identifier id.
     * Creates a new Chord ring, and add this node to it.
     * All the fingers are initialised to point to this node.
     *
     * @param id the identifier for this node.
     *           This identifier shall be generated by a consistent hash function, computed on a singular information,
     *           as IP:PORT
     * @param resourceManager
     */
    public LocalNode(int id, String host, int port, ResourceManager resourceManager) throws NetworkFailureException {
        this.id = id;
        this.host = host;
        this.port = port;
        initResourceManager(resourceManager);
        fingerTable = new FingerTable(this, this);
        successorsList = new ArrayList<>();
    }

    public LocalNode(int id, ResourceManager resourceManager) throws NetworkFailureException {
        this(id, DEFAULT_HOST, DEFAULT_PORT, resourceManager);
    }

    /**
     * Constructs a local node, by joining the Chord ring which n belongs to.
     * @param n the target node to join.
     * @param resourceManager
     * @throws NetworkFailureException when n is not available
     */
    public LocalNode(int id, String host, int port, Node n, ResourceManager resourceManager) throws NetworkFailureException {
        this.id = id;
        this.host = host;
        this.port = port;
        initResourceManager(resourceManager);
        fingerTable = new FingerTable(this, n.findSuccessor(id));
        updateSuccessorsList();
    }

    public LocalNode(int id, Node n, ResourceManager resourceManager)  throws NetworkFailureException{
        this(id, DEFAULT_HOST, DEFAULT_PORT, n, resourceManager);
    }

    private void initResourceManager(ResourceManager resourceManager) throws NetworkFailureException {
        this.resourceManager = resourceManager;
        if(resourceManager != null) {
            resourceManager.setNode(this);
        }
    }

    /*
     * Node's methods overrides
     */

    @Override
    public Node findSuccessor(int id) throws NetworkFailureException {
        if(id == getId())
            return this;
        if(!isInsideInterval(id, this.getId(), fingerTable.getSuccessor().getId()) && id != fingerTable.getSuccessor().getId()){
            Node result = null;
            try {
                result = closestPrecedingFinger(id).findSuccessor(id);
            }catch (NetworkFailureException e){
                result = findSuccessorFailureHandler(id);
            }
            if(result == null)
                throw new NetworkFailureException();
            if (result.getId() == getId())
                result = this;
            return result;
        }
        return getSuccessor();
    }

    protected Node closestPrecedingFinger(int id) {
        for(int i=M-1; i>=0; i--) {
            if (isInsideInterval(fingerTable.getNode(i).getId(), this.getId(), id))
                return fingerTable.getNode(i);
        }
        return this;
    }

    protected Node findSuccessorFailureHandler(int id) throws NetworkFailureException{
        for(int i=M-1; i>=0; i--) {
            if (isInsideInterval(fingerTable.getNode(i).getId(), this.getId(), id)) {
                try {
                    Node result = fingerTable.getNode(i).findSuccessor(id);
                    return result;
                }catch (NetworkFailureException e){
                    System.err.println("Entry "+i+" failed in node "+this.getId()+": looking for id "+id);
                }
            }
        }
        throw new NetworkFailureException("All entries failed in node "+this.getId());
    }


    /**
     * Returns a safe-to-use successor node
     * @return the successor wrapped with {@link Node#wrap()}
     */
    @Override
    public Node getSuccessor() throws NetworkFailureException {
        return _getSuccessor().wrap();
    }

    /**
     * Return the successor from the finger table.
     * For internal use, since the public version of this method would wrap the resulting node
     * @return the predecessor
     */
    private Node _getSuccessor() {
        return fingerTable.getSuccessor();
    }

    void setSuccessor(Node n) {
        //if(n.getId() != fingerTable.getSuccessor().getId()) {
            fingerTable.setNode(0, n);
        //}
    }

    /**
     * Returns a wrapped predecessor, for external use, such as further requests to it.
     * @return the wrapped predecessor
     */
    @Override
    public Node getPredecessor() throws NetworkFailureException {
        synchronized (predecessor_lock) {
            if (_getPredecessor() == null)
                return null;
            return _getPredecessor().wrap();
        }
    }

    /**
     * Return the attribute predecessor synchronizing on this.
     * For internal use, since the public version of this method would wrap the resulting node
     * @return the predecessor
     */
    public Node _getPredecessor() {
        synchronized (predecessor_lock) {
            return predecessor;
        }
    }

    void setPredecessor(Node predecessor) {
        // Apply similar reasoning as FingerTable#setNode
        synchronized (predecessor_lock) {
            Node tmp = this.predecessor;
            if (tmp != null) {
                if (predecessor != null && tmp.getId() == predecessor.getId())
                    return; // Node already inside as predecessor
                //if(tmp.getId() != this.getId())
                if(tmp instanceof RemoteNode)
                    tmp.close();
            }
            this.predecessor = predecessor;
            if (predecessor != null)
                System.out.println("New predecessor " + predecessor.getId() + " in node " + this.getId());
        }
    }

    @Override
    public void notifyPredecessor(Node n) throws NetworkFailureException {
        synchronized (predecessor_lock) {
            if (_getPredecessor() != null && _getPredecessor().getId() == n.getId())
                return;
            if (_getPredecessor() == null || isInsideInterval(n.getId(), _getPredecessor().getId(), this.getId())) {
                if (_getPredecessor() != null && _getPredecessor() instanceof RemoteNode)
                    _getPredecessor().close();
                setPredecessor(n);
            }
        }
    }

    @Override
    public void publish(ChordResource resource) {
        resourceManager.addNewResource(resource);
    }

    @Override
    public ChordResource fetch(String name) {
       return resourceManager.getFile(name);
    }

    /**
     * A local node never fails
     * @return false always
     */
    @Override
    public boolean hasFailed() throws NetworkFailureException {
        return false;
    }

    /*
     * Stabilization procedures
     */

    public void stabilize(){
        Node x;
        try {
            x = _getSuccessor().getPredecessor();
        } catch (NetworkFailureException e) {
            System.err.println("Failed to stabilize in node " + this.getId() +
                    ": Failed to get predecessor");
            return;
        }

        if(x!= null && isInsideInterval(x.getId(), getId(), fingerTable.getSuccessor().getId()))
            setSuccessor(x);

        try {
            _getSuccessor().notifyPredecessor(this);
        } catch (NetworkFailureException e) {
            System.err.println("Failed to stabilize in node " + this.getId() +
                    ": Failed to notify predecessor");
        }
    }

    public void fixFingers(){
        next = next + 1;
        if(next >= M)
            next = 0;
        try{
            Node n = findSuccessor(FingerTableEntry.initialStart(this, next, M));
            Node before = fingerTable.getNode(next);
            fingerTable.setNode(next, n);
            //if(before.getId() != n.getId())
             //   System.out.println("New entry "+next+" in node "+this.getId()+": "+before.getId()+" -> "+n.getId());
            /*
             *  Even if fixFingers cannot reach the node, will try it later by itself
             *  when `next` will have again the same value
             */
        } catch (NetworkFailureException e) {
            System.err.println("Failed to fix entry "+next +" in node "+this.getId());
        }
    }

    public void checkPredecessor() {
        synchronized (predecessor_lock) {
            if (_getPredecessor() != null) {
                try {
                    _getPredecessor().hasFailed(); // maybe "this" can be an alternative
                } catch (NetworkFailureException e) {
                    setPredecessor(null);
                    System.err.println("Predecessor failed in node " + this.getId());
                }
            }
        }
    }

    private void fixSuccessor(){
        if(successorsList == null)
            return;
        synchronized (successorsList) {
            Node before = _getSuccessor();
            System.err.println("Successor " + this._getSuccessor().getId() + " failed in node " + this.getId());
            if (successorsList.size() > 0) {
                this.setSuccessor(successorsList.get(0));
                successorsList.remove(0);
            } else {
                this.setSuccessor(this);
                System.out.println("This is the only node in the network now!");
            }

            System.out.println("New successor in node " + this.getId() + ": " + before.getId() + " -> " + this._getSuccessor().getId());
        }
    }

    public void checkSuccessor() {
        try {
            _getSuccessor().hasFailed();
        } catch(NetworkFailureException e) { // Successor has failed
            fixSuccessor();
        }
        updateSuccessorsList();
    }

    private void updateSuccessorsList(){
            List<Node> successorSuccessorsList = new ArrayList<>();
            try {
                successorSuccessorsList = this._getSuccessor().getSuccessorsList();
            } catch (NetworkFailureException e) {
                System.err.println("Failed to retrieve successorList in node " + this.getId());
            }
            List<Node> temp = (List) ((ArrayList) successorSuccessorsList).clone();
            try {
                if (this._getSuccessor().getSuccessor().getId() != this.getId() && this._getSuccessor().getSuccessor().getId() != this._getSuccessor().getId())
                    temp.add(0, this._getSuccessor().getSuccessor());
                if (temp.size() > R) {
                    temp.remove(temp.size() - 1);
                }
            } catch (NetworkFailureException e) {
                System.err.println("Failed to update successor list in node " + this.getId());
            }

            if (successorsList == null || !temp.isEmpty())
                setSuccessorsList(temp);
    }


    @Override
    public List<Node> getSuccessorsList() {
        return successorsList;
    }

    public void setSuccessorsList(List<Node> successorsList) {
        synchronized (successorsList) {
            this.successorsList = successorsList;
        }
    }


    /*
     * Getter and Setters
     */

    FingerTableEntry getFingerTableEntry(int index){
        return fingerTable.getFingerTableEntry(index);
    }

    void setFingerTableEntryNode(int index, Node n){
        fingerTable.setNode(index, n);
    }

    public int getId() {
        return id;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getIp() {
        return host;
    }

    @Override
    public void close() {
        // do nothing when you close a local node
        // closing a localnode -> update observers?
    }

    @Override
    public Node wrap() {
        return this;
        // so far return this is enough. Consider copying it and cleaning observers, but leaving same fingerTable instance
    }

    @Override
    public Boolean notifyPropagation(String title) {
        return resourceManager.isReplicaPresent(title);
    }

    @Override
    public void notifyDelete(String title) {
        resourceManager.deleteReplica(title);

    }

    @Override
    public void sendReplica(ChordResource chordResource) {
        resourceManager.addReplica(chordResource);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node pred = _getPredecessor();
        sb.append("id: ").append(id)
                .append("\nad: ").append(host).append(':').append(port)
                .append("\npred: ").append(pred == null ? "null" : pred.getId())
                .append("\nsucc list #").append(successorsList.size())
                .append(": ");
        successorsList.forEach(s -> sb.append(s.getId()).append(','));
        sb.append("\nfinger table:\n").append(fingerTable.toString());
        sb.append(resourceManager.toString());
        return sb.toString();
    }
}

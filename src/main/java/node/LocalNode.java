package node;

import network.exeptions.NetworkFailureException;
import node.exceptions.NodeNotFoundException;
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
    private Node predecessor = this; // TODO: in the Paper is null
    private int next = 0;
    private int id;
    private final String host;
    private final int port;
    private List<Node> successorsList;

    /**
     * Constructs a Node by specifying its identifier id.
     * Creates a new Chord ring, and add this node to it.
     * All the fingers are initialised to point to this node.
     *
     * @param id the identifier for this node.
     *           This identifier shall be generated by a consistent hash function, computed on a singular information,
     *           as IP:PORT
     */
    public LocalNode(int id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
        fingerTable = new FingerTable(this, this);
        successorsList = new ArrayList<>();
    }

    public LocalNode(int id) {
        this(id, DEFAULT_HOST, DEFAULT_PORT);
    }

    /**
     * Constructs a local node, by joining the Chord ring which n belongs to.
     * @param n the target node to join.
     * @throws NodeNotFoundException when n is not available
     */
    public LocalNode(int id, String host, int port, Node n) throws NodeNotFoundException, NetworkFailureException {
        this.id = id;
        this.host = host;
        this.port = port;
        fingerTable = new FingerTable(this, n.findSuccessor(id));
        updateSuccessorsList();
    }

    public LocalNode(int id, Node n) throws NodeNotFoundException, NetworkFailureException {
        this(id, DEFAULT_HOST, DEFAULT_PORT, n);
    }

    /*
     * Node's methods overrides
     */

    @Override
    public Node findSuccessor(int id) throws NodeNotFoundException, NetworkFailureException {
        if(id == getId())
            return this;
        if(!isInsideInterval(id, this.getId(), fingerTable.getSuccessor().getId()) && id != fingerTable.getSuccessor().getId()){
            try {
                Node result = closestPrecedingFinger(id).findSuccessor(id);
                if(result.getId() == getId())
                    result = this;
                return result;
            } catch (NetworkFailureException e){
                e.printStackTrace();
            }
        }
        return getSuccessor();
    }

    protected Node closestPrecedingFinger(int id) {
        for(int i=M-1; i>=0; i--) {
            if (isInsideInterval(fingerTable.getNode(i).getId(), this.getId(), id))
                /*if(fingerTable[i].getNode().hasFailed()){
                        if(i==0){
                            //TODO
                            throw new NetworkFailureException();
                        }
                        else
                            i--;
                    }
                    else*/
                return fingerTable.getNode(i);
        }
        return this;
    }

    /**
     * Returns a safe-to-use successor node
     * @return the successor wrapped with {@link Node#wrap()}
     */
    @Override
    public Node getSuccessor() throws NetworkFailureException {
        Node n = fingerTable.getSuccessor();
        return n.wrap();
    }

    void setSuccessor(Node n) {
        if(n.getId() != fingerTable.getSuccessor().getId()) {
            fingerTable.setNode(0, n);
        }
    }

    /**
     * Returns a wrapped predecessor, for external use, such as further requests to it.
     * @return the wrapped predecessor
     */
    @Override
    public Node getPredecessor() throws NetworkFailureException {
        return _getPredecessor().wrap();
    }

    /**
     * Return the attribute predecessor synchronizing on this.
     * For internal use, since the public version of this method would wrap the resulting node
     * @return the predecessor
     */
    private synchronized Node _getPredecessor() {
        // TODO should synchronize on a predecessor lock instead of this?
        return predecessor;
    }

    synchronized void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    @Override
    public void notifyPredecessor(Node n) throws NetworkFailureException {
        if(_getPredecessor() != null && _getPredecessor().getId() == n.getId())
            return;
        // TODO getPredecessor() would never be null since we initialize it as this
        if(_getPredecessor() == null || isInsideInterval(n.getId(), _getPredecessor().getId(), this.getId())) {
            if (_getPredecessor() != null)
                _getPredecessor().close();
            setPredecessor(n);
        }
    }

    @Override
    public void publish(ChordResource resource) {
        File file = new File("data/node_"+this.id + "/" +resource.getTitle());
        if(!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try(PrintWriter writer = new PrintWriter(new FileOutputStream(file))) {
            writer.print(resource.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ChordResource fetch(String name) {
        File file = new File("data/node_"+this.id + "/" +name);
        if(!file.exists())
            return new ChordResource(name, "");
        else{
            try(Scanner sc = new Scanner(new FileInputStream(file))) {
                String content = sc.nextLine();
                return new ChordResource(name, content);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new ChordResource(name, "");
    }

    /**
     * A local node never fails
     * @return false always
     */
    @Override
    public boolean hasFailed() {
        return false;
    }

    /*
     * Stabilization procedures
     */

    // TODO move externally stabilize, fixFingers and checkPredecessor
    public void stabilize(){
        try {
            Node x = fingerTable.getSuccessor().getPredecessor();
            if(isInsideInterval(x.getId(), getId(), fingerTable.getSuccessor().getId()))
                setSuccessor(x);
            fingerTable.getSuccessor().notifyPredecessor(this);
        } catch (NodeNotFoundException | NetworkFailureException e) {
            e.printStackTrace();
        }
    }

    public void fixFingers(){
        next = next + 1;
        if(next >= M)
            next = 0;
        try{
            Node n = findSuccessor(FingerTableEntry.initialStart(this, next, M));
            fingerTable.setNode(next, n);
            /*
             *  Even if fixFingers cannot reach the node, will try it later by itself
             *  when `next` will have again the same value
             */
        } catch (NodeNotFoundException | NetworkFailureException e) {
            e.printStackTrace();
        }
    }

    public void checkPredecessor() {
        if(_getPredecessor() != null && _getPredecessor().hasFailed())
            setPredecessor(null); // maybe "this" can be an alternative
    }

    public void checkSuccessor() {
        updateSuccessorsList();
        try {
            if(successorsList.size() > 0 && getSuccessor().hasFailed()) {
                this.setSuccessor(successorsList.get(0));
                successorsList.remove(0);
            }
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
    }

    private void updateSuccessorsList(){
        List<Node> successorSuccessorsList = null;
        try {
            successorSuccessorsList = this.getSuccessor().getSuccessorsList();
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
        List<Node> temp = (List) ((ArrayList) successorSuccessorsList).clone();
        try {
            if(this.getSuccessor().getSuccessor().getId() != this.getId() && this.getSuccessor().getSuccessor().getId() != this.getSuccessor().getId())
                temp.add(0, this.getSuccessor().getSuccessor());
            else if(temp.size() > R){
                temp.remove(temp.size()-1);
            }
        } catch (NodeNotFoundException | NetworkFailureException e) {
            e.printStackTrace();
        }
        setSuccessorsList(temp);
    }

    @Override
    public List<Node> getSuccessorsList() {
        return successorsList;
    }

    public void setSuccessorsList(List<Node> successorsList) {
        this.successorsList = successorsList;
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
        // TODO closing a localnode -> update observers?
    }

    @Override
    public Node wrap() {
        return this;
        // TODO so far return this is enough. Consider copying it and cleaning observers, but leaving same fingerTable instance
    }
}

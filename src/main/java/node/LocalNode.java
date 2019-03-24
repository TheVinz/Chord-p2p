package node;

import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;

import static utils.Util.M;
import static utils.Util.isInsideInterval;

public abstract class LocalNode implements Node{

    private FingerTableEntry[] fingerTable;
    private Node predecessor;
    private int id;

    public LocalNode(int id) {
        this.id = id;
        fingerTable = new FingerTableEntry[M];
        for(int i=0; i<M; i++){
            fingerTable[i] = new FingerTableEntry();
            fingerTable[i].setNode(null);
            fingerTable[i].setStart(((this.getId() + ((int) Math.pow(2,i))) % ((int) Math.pow(2, M))));
        }
    }

    public FingerTableEntry getFingerTableEntry(int index){
        return fingerTable[index];
    }

    public void setFingerTableEntryNode(int index, Node n){
        fingerTable[index].setNode(n);
    }

    public Node getSuccessor() {
        try {
            return fingerTable[0].getNode();
        } catch (FingerTableEmptyException e) {
            /*
               fingerTable[0] will never throw because is the actual successor,
               that it is initialized by the join method
             */
            return null;
        }
    }

    public void setSuccessor(Node n){
        fingerTable[0].setNode(n);

    }


    public Node closestPrecedingFinger(int id) {
        for(int i=M-1; i>=0; i--) {
            try {
                if(isInsideInterval(fingerTable[i].getNode().getId(),  this.getId(), id))
                    return fingerTable[i].getNode();
            } catch (FingerTableEmptyException e) {
                // TODO log exception
            }
        }
        return this;
    }

    public void create(){
        for(FingerTableEntry f : fingerTable)
            f.setNode(this);
        predecessor = this;
    }

    synchronized public Node getPredecessor() {
        return predecessor;
    }

    synchronized public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    abstract public void join(Node n) throws NodeNotFoundException, FingerTableEmptyException;



    public int getId() {
        return id;
    }
}

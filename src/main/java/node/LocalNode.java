package node;

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
            fingerTable[i].setStart(((this.getId() + ((int) Math.pow(2,i))) % ((int) Math.pow(2, M))));
        }
    }

    synchronized public FingerTableEntry getFingerTableEntry(int index){
        return fingerTable[index];
    }

    synchronized public void setFingerTableEntryNode(int index, Node n){
        fingerTable[index].setNode(n);
    }

    public Node getSuccessor(){
        return fingerTable[0].getNode();
    }

    public void setSuccessor(Node n){
        fingerTable[0].setNode(n);

    }


    public Node closestPrecedingFinger(int id) {
        for(int i=M-1; i>=0; i--)
            if(isInsideInterval(fingerTable[i].getNode().getId(),  this.getId(), id))
                return fingerTable[i].getNode();
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

    abstract public void join(Node n) throws NodeNotFoundException;

    public int getId() {
        return id;
    }
}

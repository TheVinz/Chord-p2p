package node;

import com.sun.istack.internal.NotNull;

import static utils.Util.*;

public class Node {

    private final int id;
    public FingerTableEntry[] fingerTable;
    private Node predecessor;

    public Node(int id) {
        this.id = id;
        fingerTable = new FingerTableEntry[M];
        for(int i=0; i<M; i++){
            fingerTable[i] = new FingerTableEntry();
            fingerTable[i].setStart(((this.id + ((int) Math.pow(2,i))) % ((int) Math.pow(2, M))));
        }
    }

    public int getId() {
        return id;
    }

    public Node findSuccessor(int id){
        Node n = findPredecessor(id);
        return n.getSuccessor();
    }

    public Node findPredecessor(int id){
        Node n = this;
        while(!isInsideInterval(id, n.getId(), n.getSuccessor().getId()) && id != n.getSuccessor().getId())
            n = n.closestPrecedingFinger(id);
        return n;
    }

    public Node closestPrecedingFinger(int id) {
        for(int i=M-1; i>=0; i--)
            if(isInsideInterval(fingerTable[i].getNode().getId(),  this.getId(), id))
                return fingerTable[i].getNode();
        return this;
    }

    public Node getSuccessor(){
        return fingerTable[0].getNode();
    }

    public void join(@NotNull Node n){
        initFingerTable(n);
        updateOthers();
    }

    public void create(){
        for(FingerTableEntry f : fingerTable)
            f.setNode(this);
        predecessor = this;
    }

    public void initFingerTable(Node n){
        fingerTable[0].setNode(n.findSuccessor(fingerTable[0].getStart()));
        predecessor = getSuccessor().getPredecessor();
        getSuccessor().setPredecessor(this);
        for(int i=1; i<M; i++){
            if(isInsideInterval(fingerTable[i].getStart(), this.id, fingerTable[i-1].getNode().getId())
                    || fingerTable[i].getStart() == this.id)
                fingerTable[i].setNode(fingerTable[i-1].getNode());
            else
                fingerTable[i].setNode(n.findSuccessor(fingerTable[i].getStart()));
        }
    }

    public void updateOthers(){
        for(int i = 0; i<M; i++){
            Node p = findPredecessor(((this.id-((int) Math.pow(2, i)) + ((int) Math.pow(2, M)))) % ((int) Math.pow(2, M)));
            p.updateFingerTable(this, i);
        }
    }

    public void updateFingerTable(Node s, int i) {
        if(isInsideInterval(s.getId(), id, fingerTable[i].getNode().getId())
                || s.getId() == id){
            fingerTable[i].setNode(s);
            Node p = predecessor;
            p.updateFingerTable(s, i);
        }
    }


    public Node getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Node n){
        this.predecessor = n;
    }
}

package test;

import com.sun.istack.internal.NotNull;
import node.Node;

import static utils.Util.*;

public class TestNode extends Node {

    public TestFingerTableEntry[] fingerTable;
    private TestNode predecessor;

    public TestNode(int id) {
        super(id);
        fingerTable = new TestFingerTableEntry[M];
        for(int i=0; i<M; i++){
            fingerTable[i] = new TestFingerTableEntry();
            fingerTable[i].setStart(((this.getId() + ((int) Math.pow(2,i))) % ((int) Math.pow(2, M))));
        }
    }


    public TestNode findSuccessor(int id){
        TestNode n = findPredecessor(id);
        return n.getSuccessor();
    }

    public TestNode findPredecessor(int id){
        TestNode n = this;
        while(!isInsideInterval(id, n.getId(), n.getSuccessor().getId()) && id != n.getSuccessor().getId())
            n = n.closestPrecedingFinger(id);
        return n;
    }

    public TestNode closestPrecedingFinger(int id) {
        for(int i=M-1; i>=0; i--)
            if(isInsideInterval(fingerTable[i].getTestNode().getId(),  this.getId(), id))
                return fingerTable[i].getTestNode();
        return this;
    }

    public TestNode getSuccessor(){
        return fingerTable[0].getTestNode();
    }

    public void join(@NotNull TestNode n){
        initFingerTable(n);
        updateOthers();
    }

    public void create(){
        for(TestFingerTableEntry f : fingerTable)
            f.setTestNode(this);
        predecessor = this;
    }

    public void initFingerTable(TestNode n){
        fingerTable[0].setTestNode(n.findSuccessor(fingerTable[0].getStart()));
        predecessor = getSuccessor().getPredecessor();
        getSuccessor().setPredecessor(this);
        for(int i=1; i<M; i++){
            if(isInsideInterval(fingerTable[i].getStart(), this.getId(), fingerTable[i-1].getTestNode().getId())
                    || fingerTable[i].getStart() == this.getId())
                fingerTable[i].setTestNode(fingerTable[i-1].getTestNode());
            else
                fingerTable[i].setTestNode(n.findSuccessor(fingerTable[i].getStart()));
        }
    }

    public void updateOthers(){
        for(int i = 0; i<M; i++){
            TestNode p = findPredecessor(((this.getId()-((int) Math.pow(2, i)) + ((int) Math.pow(2, M)))+1) % ((int) Math.pow(2, M)));
            p.updateFingerTable(this, i);
        }
    }

    public void updateFingerTable(TestNode s, int i) {
        if(isInsideInterval(s.getId(), this.getId(), fingerTable[i].getTestNode().getId())
               /* || s.getId() == id*/){
            fingerTable[i].setTestNode(s);
            TestNode p = predecessor;
            p.updateFingerTable(s, i);
        }
    }


    public TestNode getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(TestNode n){
        this.predecessor = n;
    }
}

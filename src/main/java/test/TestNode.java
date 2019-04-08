package test;

import network.exeptions.NetworkFailureException;
import node.CallTracker;
import node.LocalNode;
import node.Node;
import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static utils.Util.*;

public class TestNode extends LocalNode {



    public TestNode(int id) {
        super(id);
    }


    public Node findSuccessor(int id, CallTracker callTracker) throws FingerTableEmptyException, NetworkFailureException {
        LocalNode n = (LocalNode) findPredecessor(id);
        return n.getSuccessor();
    }

    public Node findPredecessor(int id) throws FingerTableEmptyException, NetworkFailureException {
        LocalNode n = this;
        while(!isInsideInterval(id, n.getId(), n.getSuccessor().getId()) && id != n.getSuccessor().getId())
            n = (LocalNode) n.closestPrecedingFinger(id);
        return n;
    }

    public void join(Node n) throws NodeNotFoundException, FingerTableEmptyException, NetworkFailureException {
        initFingerTable(n);
        updateOthers();
    }

    public void initFingerTable(Node n) throws NodeNotFoundException, FingerTableEmptyException, NetworkFailureException {
        this.setFingerTableEntryNode(0, n.findSuccessor(this.getFingerTableEntry(0).getStart(),null));
        setPredecessor(((LocalNode) getSuccessor()).getPredecessor());
        ((LocalNode) getSuccessor()).setPredecessor(this);
        for(int i=1; i<M; i++){
            if(this.getId() != this.getFingerTableEntry(i-1).getNode().getId() && (isInsideInterval(this.getFingerTableEntry(i).getStart(), this.getId(), this.getFingerTableEntry(i-1).getNode().getId())
                    || this.getFingerTableEntry(i).getStart() == this.getId()))
                this.setFingerTableEntryNode(i, this.getFingerTableEntry(i-1).getNode());
            else{
                Node temp = n.findSuccessor(this.getFingerTableEntry(i).getStart(), null);
                if(this.getFingerTableEntry(i).getStart() != temp.getId() && isInsideInterval(this.getId(), this.getFingerTableEntry(i).getStart(), temp.getId()))
                    temp = this;
                this.setFingerTableEntryNode(i, temp);
            }
        }
    }

    public void updateOthers() throws FingerTableEmptyException, NetworkFailureException {
        for(int i = 0; i<M; i++){
            TestNode p = (TestNode) findPredecessor(((this.getId()-((int) Math.pow(2, i)) + ((int) Math.pow(2, M)))+1) % ((int) Math.pow(2, M)));
            p.updateFingerTable(this, i);
        }
    }

    public void exit() throws FingerTableEmptyException, NetworkFailureException {
        List<TestNode> targetNodes = new ArrayList<>();
        List<TestNode> updatedNodes = new ArrayList<>();
        for(int i = 0; i<M; i++){
            TestNode p = (TestNode) findPredecessor(((this.getId()-((int) Math.pow(2, i)) + ((int) Math.pow(2, M)))+1) % ((int) Math.pow(2, M)));
            targetNodes.add(p);
            updatedNodes.add((TestNode) this.getSuccessor());
        }
        for(int i = 0; i<M; i++){
            TestNode temp = targetNodes.get(i);
            while(temp.getFingerTableEntry(i).getNode().getId() == this.getId()){
                temp.getFingerTableEntry(i).setNode(updatedNodes.get(i));
                temp = (TestNode) temp.getPredecessor();
            }
        }
        ((TestNode)this.getSuccessor()).setPredecessor(getPredecessor());
    }

    public void updateFingerTable(Node s, int i) throws FingerTableEmptyException, NetworkFailureException {
        if(isInsideInterval(s.getId(), this.getId(), this.getFingerTableEntry(i).getNode().getId())){
            this.setFingerTableEntryNode(i, s);
            TestNode p = (TestNode) getPredecessor();
            p.updateFingerTable(s, i);
        }
    }

}

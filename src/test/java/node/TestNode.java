package node;

import network.exeptions.NetworkFailureException;
import node.exceptions.NodeNotFoundException;

import java.util.Objects;

import static utils.Util.M;
import static utils.Util.isInsideInterval;

public class TestNode extends LocalNode {



    public TestNode(int id) {
        super(id);
    }


    public Node findSuccessor(int id) {
        LocalNode n = (LocalNode) findPredecessor(id);
        return n.getSuccessor();
    }

    public Node findPredecessor(int id) {
        LocalNode n = this;
        while(!isInsideInterval(id, n.getId(), n.getSuccessor().getId()) && id != n.getSuccessor().getId())
            n = (LocalNode) n.closestPrecedingFinger(id);
        return n;
    }

    public void join(Node n) throws NodeNotFoundException, NetworkFailureException {
        Objects.requireNonNull(n);
        initFingerTable(n);
        updateOthers();
    }

    public void initFingerTable(Node n) throws NodeNotFoundException, NetworkFailureException {
        this.setFingerTableEntryNode(0, n.findSuccessor(this.getFingerTableEntry(0).getStart()));
        setPredecessor(((LocalNode) getSuccessor()).getPredecessor());
        ((LocalNode) getSuccessor()).setPredecessor(this);
        for(int i=1; i<M; i++){
            if(this.getId() != this.getFingerTableEntry(i-1).getNode().getId() && (isInsideInterval(this.getFingerTableEntry(i).getStart(), this.getId(), this.getFingerTableEntry(i-1).getNode().getId())
                    || this.getFingerTableEntry(i).getStart() == this.getId()))
                this.setFingerTableEntryNode(i, this.getFingerTableEntry(i-1).getNode());
            else{
                Node temp = n.findSuccessor(this.getFingerTableEntry(i).getStart());
                if(this.getFingerTableEntry(i).getStart() != temp.getId() && isInsideInterval(this.getId(), this.getFingerTableEntry(i).getStart(), temp.getId()))
                    temp = this;
                this.setFingerTableEntryNode(i, temp);
            }
        }
    }

    public void updateOthers() {
        for(int i = 0; i<M; i++){
            TestNode p = (TestNode) findPredecessor(((this.getId()-((int) Math.pow(2, i)) + ((int) Math.pow(2, M)))+1) % ((int) Math.pow(2, M)));
            p.updateFingerTable(this, i);
        }
    }

    public void updateFingerTable(Node s, int i) {
        if(isInsideInterval(s.getId(), this.getId(), this.getFingerTableEntry(i).getNode().getId())){
            this.setFingerTableEntryNode(i, s);
            TestNode p = (TestNode) getPredecessor();
            p.updateFingerTable(s, i);
        }
    }

}

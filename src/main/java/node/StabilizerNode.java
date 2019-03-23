package node;

import static utils.Util.M;
import static utils.Util.isInsideInterval;

public class LocalNode extends Node {

    private FingerTableEntry[] fingerTable;

    protected LocalNode(int id) {
        super(id);
    }

    public Node getSuccessor(){
        return fingerTable[0].getRemoteNode();
    }

    public Node findSuccessor(int id) throws NodeNotFoundException{
        if(!isInsideInterval(id, this.getId(), this.getSuccessor().getId()) && id != this.getSuccessor().getId()){
            Node temp = closestPrecedingFinger(id);
            return temp.findSuccessor(id);
        }
        return getSuccessor();
    }

    public Node closestPrecedingFinger(int id) {
        for(int i=M-1; i>=0; i--)
            if(isInsideInterval(fingerTable[i].getRemoteNode().getId(),  this.getId(), id))
                return fingerTable[i].getRemoteNode();
        return this;
    }
}

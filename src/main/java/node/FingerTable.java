package node;

import network.remoteNode.RemoteNode;
import utils.Util;

public class FingerTable {

    private FingerTableEntry[] entries = new FingerTableEntry[Util.M];

    FingerTable(Node node){
        for(int i=0; i<entries.length; i++){
            entries[i]=new FingerTableEntry();
            entries[i].setNode(node);
            entries[i].setStart(((node.getId() + ((int) Math.pow(2,i))) % ((int) Math.pow(2, Util.M))));
        }
    }

    Node getNode(int index) {
        return entries[index].getNode();
    }

    void setNode(int index, Node n) {
            Node old = entries[index].getNode(), newNode=null;
            if(old.getId()==n.getId())
                return;
            for(int i=0; i<entries.length && newNode==null; i++){
                if(entries[i].getNode().getId()==n.getId())
                    newNode=entries[i].getNode();
            }

            if(newNode==null){
                newNode = n;
            }

            entries[index].setNode(newNode);

            if(old instanceof RemoteNode) {
                for(FingerTableEntry entry : entries)
                    if(entry.getNode().getId()==old.getId())
                        return;
                ((RemoteNode) old).close();
            }
    }

    Node getSuccessor() {
        return getNode(0);
    }
}

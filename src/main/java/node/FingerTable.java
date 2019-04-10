package node;

import utils.Util;

public class FingerTable {

    private FingerTableEntry[] entries = new FingerTableEntry[Util.M];

    public FingerTable(Node node){
        for(int i=0; i<entries.length; i++){
            entries[i]=new FingerTableEntry();
            entries[i].setNode(node);
            entries[i].setStart(((node.getId() + ((int) Math.pow(2,i))) % ((int) Math.pow(2, Util.M))));
        }
    }

    public Node getNode(int index) {
        return entries[index].getNode();
    }

    public void setNode(int index, Node n) {
            Node old = entries[index].getNode(), newNode=null;
            for(int i=0; i<entries.length && newNode==null; i++){
                if(entries[i].getNode().getId()==n.getId())
                    newNode=entries[i].getNode();
            }
            if(newNode==null){
                newNode = n;
            }
            else{
                Util.closeNodeConnection(n,this);
            }
            entries[index].setNode(newNode);
            Util.closeNodeConnection(old, this);
    }

    public void print() {
        System.out.println("\n_______________________________________\n");
        for(FingerTableEntry entry : entries){
            System.out.println(entry.getStart() +  " --> " + entry.getNode().getId());
        }
        System.out.println("\n_______________________________________\n");
    }

    public boolean contains(Node n){
        for (FingerTableEntry entry : entries) {
            if (entry.getNode().getId() == n.getId())
                return true;
        }
        return false;
    }
}

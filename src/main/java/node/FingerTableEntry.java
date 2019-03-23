package node;

import test.TestNode;

public class FingerTableEntry {
    private int start;
    private Node node;

    public FingerTableEntry(){}

    public FingerTableEntry(int start, Node node){
        this.start = start;
        this.node = node;
    }

    synchronized public void setNode(Node node) {
        this.node = node;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStart() {
        return start;
    }

    synchronized public Node getNode() {
        return node;
    }
}

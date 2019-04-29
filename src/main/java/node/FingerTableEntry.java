package node;

import java.util.Objects;

public class FingerTableEntry {
    private int start;
    private Node node;

    public FingerTableEntry(int start, Node node){
        this.start = start;
        setNode(node);
    }

    synchronized public void setNode(Node node) {
        Objects.requireNonNull(node);
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


    /**
     * Computes the expected id that a node in position index (in the fingerTable of ftOwner)
     * would have if all the 2^m nodes are present in the network
     * @param ftOwner the owner of the finger table
     * @param index the index of the position
     * @param m the number of bit allowed for ids
     * @return an integer between 0 and 2^n - 1
     */
    static int initialStart(Node ftOwner, int index, int m) {
        return (ftOwner.getId() + (int) Math.pow(2,index)) % (int) Math.pow(2, m);
    }

}

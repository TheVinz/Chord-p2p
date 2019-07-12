package node;

import network.remoteNode.RemoteNode;
import utils.Util;

import java.util.Objects;
import java.util.Optional;

public class FingerTable {

    // TODO use a synchronized structure because an update could happen during a request?
    private FingerTableEntry[] entries = new FingerTableEntry[Util.M];
    private Node node;

    /**
     * Constructs populating the keys ({@link FingerTableEntry#setStart(int)}
     * of the entries in the fingerTable, computing them from the entry's index and {@link utils.Util#M}
     * @param node the node with respect to compute the fingers key.
     * @param initNode the node to initialise each fingers.
     */
    FingerTable(Node node, Node initNode) {
        this.node = node;
        Objects.requireNonNull(node);
        for(int i=0; i<entries.length; i++)
            entries[i] = new FingerTableEntry(FingerTableEntry.initialStart(node, i, Util.M),
                    initNode);
        System.out.println("Finger table in node "+node.getId()+" initialized with "+initNode.getId());
    }

    Node getNode(int index) {
        return entries[index].getNode();
    }

    void setNode(int index, Node n) {
        Objects.requireNonNull(n);
        Node old = entries[index].getNode(), newNode=null;

        if(old.getId() == n.getId())
            return;

        System.out.println("New entry "+index+" in node "+node.getId()+": "+old.getId()+" -> "+n.getId());

        // Search if n alredy present
        for(int i=0; i<entries.length && newNode==null; i++){
            if(entries[i].getNode().getId()==n.getId())
                // then re-use the instance
                newNode=entries[i].getNode();
        }

        // In case there wasn't
        if(newNode == null) {
            newNode = n;
        }

        // Insert the instance
        entries[index].setNode(newNode);

        // search for the old node in index-th position
        for(FingerTableEntry entry : entries)
            if(entry.getNode().getId() == old.getId())
                // found it in other position, don't close it
                return;
        if(old instanceof RemoteNode)
            old.close(); // not other instances of the old one
    }

    Node getSuccessor() {
        return getNode(0);
    }

    /**
     * Finds the first node whose id is equal to the param
     * @param id the id of the node to find
     * @return an {@link Optional<Node>} containing the node if present, otherwise an empty one
     */
    private Optional<Node> findNodeById(int id) {
        for (FingerTableEntry entry : entries) {
            if (entry.getNode().getId() == id)
                return Optional.ofNullable(entry.getNode());
        }
        return Optional.empty();
    }

    FingerTableEntry getFingerTableEntry(int index) {
        return entries[index];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %4s | %5s | %7s |\n",
                "i-th", "start", "node id"));
        sb.append("+------+-------+---------+\n");
        int i = 0;
        for (FingerTableEntry e: entries)
            sb.append(String.format("| %4d | %5d | %7d |\n",
                    i++, e.getStart(), e.getNode().getId()));
        return sb.toString();
    }
}

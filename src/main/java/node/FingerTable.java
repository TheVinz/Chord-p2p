package node;

import utils.Util;

import java.util.Objects;
import java.util.Optional;

public class FingerTable {

    // TODO use a synchronized structure because an update could happen during a request?
    private FingerTableEntry[] entries = new FingerTableEntry[Util.M];

    /**
     * Constructs populating the keys ({@link FingerTableEntry#setStart(int)}
     * of the entries in the fingerTable, computing them from the entry's index and {@link utils.Util#M}
     * @param node the node with respect to compute the fingers key.
     * @param initNode the node to initialise each fingers.
     */
    FingerTable(Node node, Node initNode) {
        Objects.requireNonNull(node);
        for(int i=0; i<entries.length; i++)
            entries[i] = new FingerTableEntry(FingerTableEntry.initialStart(node, i, Util.M),
                    initNode);
    }

    Node getNode(int index) {
        return entries[index].getNode();
    }

    void setNode(int index, Node n) {
        Objects.requireNonNull(n);
        Node old = entries[index].getNode();

        if(old.getId() == n.getId())
            return;

        if(!contains(n))
            entries[index].setNode(n);

        for(FingerTableEntry entry : entries)
            if(entry.getNode().getId() == old.getId())
                return;
        old.close();
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

    private boolean contains(Node n){
        return findNodeById(n.getId()).isPresent();
    }

    FingerTableEntry getFingerTableEntry(int index) {
        return entries[index];
    }
}

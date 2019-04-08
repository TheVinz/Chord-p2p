package node;

import network.exeptions.NetworkFailureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import utils.Util;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Some basic unit tests about the {@link LocalNode} logic.
 */
class LocalNodeTest {

    /**
     * When a node creates a chord ring, test that each finger points to itself.
     * Instead, the predecessor does not exists yet
     */
    @Test
    void testCreateSelfLink() {
        LocalNode ln = new LocalNode(0);
        Executable[] asserts = new Executable[Util.M];
        CallTracker ct = new CallTracker(0, 0);
        ln.create();

        for (int i = 0; i < Util.M; i++) {
            int finalI = i;
            asserts[finalI] = () -> assertEquals(ln, ln.findSuccessor(finalI, ct));
        }
        // TODO: asserts[Util.M] = () -> assertNull(ln.getPredecessor());

        assertAll(asserts);
    }

    /**
     * Tests that joining a single node ring, the successor would be set actually to that node.
     * The predecessor is still not initialized yet.
     */
    @Test
    void testJoinSuccessorOneNode() {
        LocalNode a = new LocalNode(0);
        LocalNode b = new LocalNode(5);

        a.create();
        assertDoesNotThrow(() -> b.join(a));
        // TODO: assertNull(b.getPredecessor());
        Node bSucc = null;
        try {
            bSucc = b.getSuccessor();
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
        assertEquals(a, bSucc);
    }

    /**
     * Tests that once a node joins, the {@link LocalNode#stabilize()} works well
     * in order to fix predecessors and successors.
     * (Indirectly it tests the {@link LocalNode#notifyPredecessor(Node)} as well)
     */
    @Test
    void testStabilization2Nodes() {
        LocalNode a = new LocalNode(0);
        LocalNode b = new LocalNode(5);
        LocalNode x = new LocalNode(3);

        a.create();
        assertDoesNotThrow(() -> b.join(a)); // b.successor = a

        // Recreate a stable state
        a.setPredecessor(b);
        a.setSuccessor(b);
        b.setPredecessor(a);

        // x join, then ring unstable
        assertDoesNotThrow(() -> x.join(b)); // x.successor = 5

        x.stabilize(); // fix b's predecessor through notify
        a.stabilize(); // fix a's successor + x's predecessor through notify

        // verify that the stability is reached
        Executable[] asserts = new Executable[]{
                () -> assertEquals(5, a.getPredecessor().getId()),
                () -> assertEquals(3, a.getSuccessor().getId()),
                () -> assertEquals(0, x.getPredecessor().getId()),
                () -> assertEquals(5, x.getSuccessor().getId()),
                () -> assertEquals(3, b.getPredecessor().getId()),
                () -> assertEquals(0, b.getSuccessor().getId())
        };
        assertAll(asserts);
    }

}

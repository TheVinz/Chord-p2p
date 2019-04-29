package node;

import network.exeptions.NetworkFailureException;
import node.exceptions.NodeNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;
import static utils.Util.M;
import static utils.Util.createDefaultStabilizerNode;

public class Tester {

    private static final long[] delays = new long[]{50, 80};
    private static final long[] periods = new long[]{25, 25};

    private List<Node> stabilizerNodes = new ArrayList<>();
    private List<Node> testNodes = new ArrayList<>();
    private StabilizerNode stabilizerSource;
    private TestNode testSource;

    public static void main(String[] args) {
        Tester tester = new Tester();
        try {
            tester.test();
        } catch (NodeNotFoundException | NetworkFailureException e) {
            e.printStackTrace();
        }
    }

    public void test() throws NodeNotFoundException, NetworkFailureException {
        setup();
        checkSuccessor(stabilizerSource, testSource);
        checkFingerTable(stabilizerNodes, testNodes);
        tearDown();
    }

    private void setup() {
        int n = (int) Math.pow(2,M);
        boolean[] booleans = new boolean[n];
        for(int i=0; i<booleans.length; i++)
            booleans[i]=true;
        booleans[0] = false;

        stabilizerSource = createDefaultStabilizerNode(0, delays, periods);
        stabilizerSource.start();
        stabilizerNodes.add(stabilizerSource);

        testSource = new TestNode(0);
        testNodes.add(testSource);

        Random rnd = new Random(9);
        for(int i=0; i<10; i++){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int id = rnd.nextInt(n);
            if(booleans[id]) {
                TestNode testNode = new TestNode(id);
                testNodes.add(testNode);
                try {
                    StabilizerNode stabilizerNode = createDefaultStabilizerNode(id, stabilizerSource, delays, periods);
                    stabilizerNodes.add(stabilizerNode);
                    stabilizerNode.start();
                    testNode.join(testSource);

                } catch (NodeNotFoundException | NetworkFailureException e) {
                    e.printStackTrace();
                    System.err.println("Sorry, join failed.\nTry again later.\nExiting..");
                    System.exit(-1);
                    // TODO Try again after x seconds.
                }
                System.out.println(id + " created");
                booleans[id] = false;
            }
        }
        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void tearDown() {
        for (Node node : stabilizerNodes)
            ((StabilizerNode)node).stop();
    }

    public void checkFingerTable(List<Node> stabilizerNodes, List<Node> testNodes) {
        for(int i=0; i<stabilizerNodes.size(); i++){
            for(int j=0; j<M; j++){
                int id1 = ((StabilizerNode) stabilizerNodes.get(i)).getFingerTableEntry(j).getNode().getId();
                int id2 = ((TestNode) testNodes.get(i)).getFingerTableEntry(j).getNode().getId();
                if( id1 != id2 )
                    System.err.println("Wrong finger table for node "+stabilizerNodes.get(i).getId()+" at index "+j+" Test: "+id2+" Stabilizer: "+id1);
            }
        }
        System.out.println("checkFingerTable is finished!");
    }

    public void checkSuccessor(Node stabilizerSource, Node testSource) throws NodeNotFoundException, NetworkFailureException {
        Node stabilizerNode = stabilizerSource.getSuccessor();
        Node testNode = testSource.getSuccessor();
        int cont = 0;
        while(stabilizerNode.getId() != stabilizerSource.getId()){
            cont++;
            stabilizerNode = stabilizerNode.getSuccessor();
            testNode = testNode.getSuccessor();
            if(stabilizerNode.getId() != testNode.getId())
                System.err.println("Wrong successor!");
        }
        if(cont == 0)
            System.err.println("Wrong successor!");
        System.out.println("checkSuccessor is finished!");
    }
}

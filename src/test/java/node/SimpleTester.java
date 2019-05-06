package node;

import network.exceptions.NetworkFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;
import static utils.Util.M;
import static utils.Util.createDefaultStabilizerNode;

public class SimpleTester {

    private static final long[] delays = new long[]{500, 800, 0, 100};
    private static final long[] periods = new long[]{250, 250, 20, 20};

    private List<Node> stabilizerNodes = new ArrayList<>();
    private List<Node> testNodes = new ArrayList<>();
    private StabilizerNode stabilizerSource;
    private TestNode testSource;

    public static void main(String[] args) {
        SimpleTester tester = new SimpleTester();
        try {
            tester.test();
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
    }

    public void test() throws NetworkFailureException {
        setup();
        checkSuccessor(stabilizerSource, testSource);
        checkFingerTable(stabilizerNodes, testNodes);
        tearDown();
    }

    enum Edge {
        E1, E2, E3
    }

    public void exitNode(List<Node> testNodes, List<Node> stabilizerNodes, int id, boolean booleans[]){

    }

    private void checkTesterCorrectness() throws  NetworkFailureException {
        Node temp = testNodes.get(0);
        int begin = temp.getId();
        int cont = 1;
        //System.out.println(begin);
        while(temp.getSuccessor().getId() != begin){
            cont ++;
            temp = temp.getSuccessor();
            //System.out.println(temp.getId());
            boolean ctrl = false;
            for(int j=0; j<testNodes.size(); j++)
                if(testNodes.get(j).getId() == temp.getId()){
                    ctrl = true;
                    break;
                }
            if(!ctrl)
                System.err.println("The tester is wrong!");
        }
        if(cont != testNodes.size()){
            System.err.println("Size error, the tester is wrong!");
        }
    }

    public StabilizerNode createStabilizerNode(int id, long[] delays, long[] periods){
        return createDefaultStabilizerNode(id, delays, periods, false);
    }

    public StabilizerNode createStabilizerNode(int id, Node node, long[] delays, long[] periods) throws  NetworkFailureException {
        return createDefaultStabilizerNode(id, node, delays, periods, false);
    }

    private void setup() {
        Edge[][] map = new Edge[1][2];
        int n = (int) Math.pow(2,M);
        boolean[] booleans = new boolean[n];
        for(int i=0; i<booleans.length; i++)
            booleans[i]=true;
        booleans[0] = false;

        stabilizerSource = createStabilizerNode(0, delays, periods);
        stabilizerSource.start();
        stabilizerNodes.add(stabilizerSource);

        testSource = new TestNode(0);
        testNodes.add(testSource);

        Random rnd = new Random(10);
        for(int i=0; i<20; i++){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int id = rnd.nextInt(n);
            while(id == 0)
                id = rnd.nextInt(n);
            if(booleans[id]) {
                TestNode testNode = new TestNode(id);
                testNodes.add(testNode);
                boolean correctJoin = false;
                while(!correctJoin) {
                    try {
                        StabilizerNode stabilizerNode = createStabilizerNode(id, stabilizerSource, delays, periods);
                        stabilizerNodes.add(stabilizerNode);
                        stabilizerNode.start();
                        testNode.join(testSource);
                        correctJoin = true;

                    } catch (NetworkFailureException e) {
                        e.printStackTrace();
                        System.err.println("Sorry, join of node "+id+" failed.\nTry again...\n");
                        try {
                            sleep(2000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        // TODO Try again after x seconds.
                    }
                }
                System.out.println(id + " created");
                booleans[id] = false;
            }else{
                exitNode(testNodes, stabilizerNodes, id, booleans);
            }
        }
        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            checkTesterCorrectness();
        } catch (NetworkFailureException e) {
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

    public void checkSuccessor(Node stabilizerSource, Node testSource) throws  NetworkFailureException {
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

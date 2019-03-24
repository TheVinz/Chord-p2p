package test;

import node.Node;
import node.StabilizerNode;
import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;
import static utils.Util.M;

public class Tester {


    public void test() throws FingerTableEmptyException {
        List<Node> stabilizerNodes = new ArrayList<Node>();
        List<Node> testNodes = new ArrayList<Node>();
        int n = (int) Math.pow(2,M);
        boolean[] booleans = new boolean[n];
        for(int i=0; i<booleans.length; i++)
            booleans[i]=true;
        booleans[0] = false;
        StabilizerNode stabilizerSource = new StabilizerNode(0);
        stabilizerSource.create();
        stabilizerNodes.add(stabilizerSource);
        TestNode testSource = new TestNode(0);
        testSource.create();
        testNodes.add(testSource);
        Random rnd = new Random(12);
        for(int i=0; i<100; i++){
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int id = rnd.nextInt(n);
            if(booleans[id]) {
                Node stabilizerNode = new StabilizerNode(id);
                stabilizerNodes.add(stabilizerNode);
                Node testNode = new TestNode(id);
                testNodes.add(testNode);
                try {
                    ((StabilizerNode) stabilizerNode).join(stabilizerSource);
                    ((TestNode) testNode).join(testSource);

                } catch (FingerTableEmptyException e) {
                    e.printStackTrace();
                    System.err.println("Sorry, join failed.\nTry again later.\nExiting..");
                    System.exit(-1);
                    // TODO Try again after x seconds.
                } catch (NodeNotFoundException e) {
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
        checkSuccessor(stabilizerSource, testSource);
        checkFingerTable(stabilizerNodes, testNodes);
    }

    public void checkFingerTable(List<Node> stabilizerNodes, List<Node> testNodes) throws FingerTableEmptyException {
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

    public void checkSuccessor(Node stabilizerSource, Node testSource){
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

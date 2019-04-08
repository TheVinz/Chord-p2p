package test;

import network.exeptions.NetworkFailureException;
import node.Node;
import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;

import java.util.List;

public class FailureTester extends SimpleTester {

    public void exitNode(List<Node> testNodes, int id, boolean booleans[]){
        for(int j=0; j<testNodes.size(); j++)
            if(testNodes.get(j).getId() == id){
                try {
                    ((TestNode)testNodes.get(j)).exit();
                    System.out.println(id + " exited");
                    booleans[id] = true;
                    testNodes.remove(j);
                    break;
                } catch (FingerTableEmptyException e) {
                    e.printStackTrace();
                } catch (NetworkFailureException e) {
                    e.printStackTrace();
                }

            }
    }

    public void test() throws NodeNotFoundException, NetworkFailureException, FingerTableEmptyException {
        super.test();
    }


}

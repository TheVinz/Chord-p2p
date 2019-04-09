package test;

import network.exeptions.NetworkFailureException;
import node.Node;
import node.StabilizerNode;
import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;

import java.util.List;

import static utils.Util.createDefaultStabilizerNode;

public class FailureTester extends SimpleTester {

    public void exitNode(List<Node> testNodes, List<Node> stabilizerNodes, int id, boolean booleans[]){
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
        for(int j=0; j<stabilizerNodes.size(); j++)
            if(stabilizerNodes.get(j).getId() == id){
                ((FailingNode)stabilizerNodes.get(j)).setHasFailed(true);
                stabilizerNodes.remove(j);
                break;
            }
    }

    @Override
    public void test() throws NodeNotFoundException, NetworkFailureException, FingerTableEmptyException {
        super.test();
    }

    @Override
    public StabilizerNode createStabilizerNode(int id, long[] delays, long periods[]){
        return createDefaultStabilizerNode(id, delays, periods, true);
    }


}

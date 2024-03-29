package node;

import network.exceptions.NetworkFailureException;

import java.util.List;

import static utils.Util.createDefaultStabilizerNode;

public class FailureTester extends SimpleTester {

    public static void main(String[] args) {
        FailureTester failureTester = new FailureTester();
        try {
            failureTester.test();
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
    }

    public void exitNode(List<Node> testNodes, List<Node> stabilizerNodes, int id, boolean booleans[]){
        for(int j=0; j<testNodes.size(); j++)
            if(testNodes.get(j).getId() == id) {
                try {
                    ((TestNode)testNodes.get(j)).exit();
                } catch (NetworkFailureException e) {
                    e.printStackTrace();
                    System.err.println("Error on exiting during test");
                    System.exit(-1);
                }
                System.out.println(id + " exited");
                booleans[id] = true;
                testNodes.remove(j);
                break;
            }
        for(int j=0; j<stabilizerNodes.size(); j++)
            if(stabilizerNodes.get(j).getId() == id){
                ((FailingNode)stabilizerNodes.get(j)).setHasFailed(true);
                stabilizerNodes.remove(j);
                break;
            }
    }

    @Override
    public void test() throws NetworkFailureException {
        super.test();
    }

    @Override
    public StabilizerNode createStabilizerNode(int id, long[] delays, long[] periods) throws NetworkFailureException {
        return createDefaultStabilizerNode(id, delays, periods, true, null);
    }

    @Override
    public StabilizerNode createStabilizerNode(int id, Node node, long[] delays, long[] periods) throws NetworkFailureException {
        return createDefaultStabilizerNode(id, node, delays, periods, true, null);
    }

}

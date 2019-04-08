import network.exeptions.NetworkFailureException;
import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;
import test.FailureTester;

public class Main {
    public static void main(String[] args) {

        /*SimpleTester simpleTester = new SimpleTester();
        try {
            simpleTester.test();
        } catch (FingerTableEmptyException | NodeNotFoundException | NetworkFailureException e) {
            e.printStackTrace();
        }*/
        FailureTester failureTester = new FailureTester();
        try {
            failureTester.test();
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        } catch (FingerTableEmptyException e) {
            e.printStackTrace();
        }


    }
}

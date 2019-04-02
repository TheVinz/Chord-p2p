import network.exeptions.NetworkFailureException;
import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;
import test.Tester;

public class Main {
    public static void main(String[] args) {

        Tester tester = new Tester();
        try {
            tester.test();
        } catch (FingerTableEmptyException | NodeNotFoundException | NetworkFailureException e) {
            e.printStackTrace();
        }


    }
}

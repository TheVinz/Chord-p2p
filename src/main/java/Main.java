import node.Node;
import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;
import node.StabilizerNode;
import test.Tester;

import java.util.Random;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws NodeNotFoundException {

        Tester tester = new Tester();
        try {
            tester.test();
        } catch (FingerTableEmptyException e) {
            e.printStackTrace();
        }


    }
}

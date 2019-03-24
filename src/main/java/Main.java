import node.Node;
import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;
import node.StabilizerNode;

import java.util.Random;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws NodeNotFoundException {

        /*TestNode source = new TestNode(0);
        source.create();
        new TestNode(2).join(source);
        for(FingerTableEntry f : source.findSuccessor(2).fingerTable){
            System.out.println(f.getStart() + " -> " + f.getNode().getId());
        }*/


        /*
        boolean[] booleans = new boolean[32];
        for(int i=0; i<booleans.length; i++)
            booleans[i]=true;
        booleans[0] = false;
        TestNode source = new TestNode(0);
        source.create();

        for(int i=0; i<10; i++){
            int id = new Random().nextInt(32);
            if(booleans[id]) {
                TestNode n = new TestNode(id);
                n.join(source);
                System.out.println(id + " created");
                booleans[id] = false;
            }
        }

        System.out.println(0);
        Node n = source.getSuccessor();
        while(n.getId() != source.getId()){
            System.out.println(n.getId());
            n=n.getSuccessor();
        }*/

        boolean[] booleans = new boolean[32];
        for(int i=0; i<booleans.length; i++)
            booleans[i]=true;
        booleans[0] = false;
        StabilizerNode source = new StabilizerNode(0);
        source.create();

        for(int i=0; i<10; i++){
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int id = new Random().nextInt(32);
            if(booleans[id]) {
                StabilizerNode n = new StabilizerNode(id);
                try {
                    n.join(source);

                } catch (FingerTableEmptyException e) {
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
        System.out.println(0);
        Node n = source.getSuccessor();
        while(n.getId() != source.getId()){
            System.out.println(n.getId());
            n=n.getSuccessor();
        }


    }
}

import node.FingerTableEntry;
import node.Node;

import java.util.Random;

public class Main {
    public static void main(String[] args){

        /*Node source = new Node(0);
        source.create();
        new Node(2).join(source);
        for(FingerTableEntry f : source.findSuccessor(2).fingerTable){
            System.out.println(f.getStart() + " -> " + f.getNode().getId());
        }*/



        boolean[] booleans = new boolean[32];
        for(int i=0; i<booleans.length; i++)
            booleans[i]=true;
        booleans[0] = false;
        Node source = new Node(0);
        source.create();

        for(int i=0; i<10; i++){
            int id = new Random().nextInt(32);
            if(booleans[id]) {
                Node n = new Node(id);
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
        }
    }
}

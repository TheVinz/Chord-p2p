package node;

import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.pow;
import static utils.Util.M;
import static utils.Util.isInsideInterval;

public class StabilizerNode extends LocalNode implements Notifier{

    private int next = 0;
    private boolean newEntry = true;

    public StabilizerNode(int id) {
        super(id);

        TimerTask stabilizeTask = new TimerTask() {
            @Override
            public void run() {
                stabilize();
            }
        };
        TimerTask fixFingerTask = new TimerTask() {
            @Override
            public void run() {
                fixFingers();
                if(newEntry){
                    while(next != 0){
                        fixFingers();
                    }
                    newEntry = false;
                }
            }
        };
        Timer stabilizeTimer = new Timer("Stabilizer");
        stabilizeTimer.scheduleAtFixedRate(stabilizeTask, 500, 250);
        Timer fixFingerTimer = new Timer("FixFinger");
        fixFingerTimer.scheduleAtFixedRate(fixFingerTask, 800, 250);

    }

    public void join(Node n) throws NodeNotFoundException, FingerTableEmptyException {
        setPredecessor(null);
        setSuccessor(n.findSuccessor(this.getId()));
    }

    public Node findSuccessor(int id) throws NodeNotFoundException, FingerTableEmptyException {
        if(!isInsideInterval(id, this.getId(), this.getSuccessor().getId()) && id != this.getSuccessor().getId()){
            Node temp = closestPrecedingFinger(id);
            return temp.findSuccessor(id);
        }
        return getSuccessor();
    }

    public void stabilize(){
        try {
            Node x = getSuccessor().getPredecessor();
            if(isInsideInterval(x.getId(), this.getId(), this.getSuccessor().getId()))
                setSuccessor(x);
            ((Notifier) getSuccessor()).notifyPredecessor(this);
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void notifyPredecessor(Node n){
        if(getPredecessor() == null || isInsideInterval(n.getId(), getPredecessor().getId(), this.getId()))
            setPredecessor(n);

    }

    public void fixFingers(){
        next = next + 1;
        if(next >= M)
            next = 0;
        try {
            this.setFingerTableEntryNode(next, findSuccessor((this.getId()+(int)pow(2,next))%((int)pow(2,M))));
            /*
                Even if fixFingers cannot reach the node, will try it later by itself
                when `next` will have again the same value
             */
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        } catch (FingerTableEmptyException e) {
            e.printStackTrace();
        }
    }

    public void checkPredecessor(){
        //
    }

}

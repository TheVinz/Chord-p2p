package node;

import network.exeptions.NetworkFailureException;
import node.exceptions.FingerTableEmptyException;
import node.exceptions.NodeNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static utils.Util.R;

public class FailureHandlerNode extends StabilizerNode {

    private List<Node> successorsList;
    /**
     * Constructs a Node specifying the periodic actions to execute in order to stabilize it.
     *
     * @param id      the node id
     * @param tasks   the sequence of actions to execute on a {@link LocalNode}
     * @param labels  the labels to assign to each action (w.r.t. to the order of tasks)
     * @param delays  the amount of time in milliseconds before scheduling each task (w.r.t. to the order of tasks)
     * @param periods the amount of time in milliseconds among each repetition of the tasks execution
     */
    public FailureHandlerNode(int id, Consumer<LocalNode>[] tasks, String[] labels, long[] delays, long[] periods) {
        super(id, tasks, labels, delays, periods);
    }

    public void checkPredecessor() {
        try {
            if(super.getPredecessor()!= null && super.getPredecessor().hasFailed())
                setPredecessor(null); // maybe "this" can be an alternative
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
    }

    synchronized public void checkSuccessor() {
        try {
            if(successorsList.size() > 0 && super.getSuccessor().hasFailed()) {
                this.setSuccessor(successorsList.get(0));
                successorsList.remove(0);
            }
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
    }

    synchronized private void updateSuccessorsList(){
        List<Node> successorSuccessorsList = null;
        try {
            successorSuccessorsList = ((FailureHandlerNode) this.getSuccessor()).getSuccessorsList();
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
        List<Node> temp = (List) ((ArrayList) successorSuccessorsList).clone();
        try {
            if(this.getSuccessor().getSuccessor().getId() != this.getId())
                temp.add(0, this.getSuccessor().getSuccessor());
            else if(temp.size() > R){
                temp.remove(temp.size()-1);
            }
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
        setSuccessorsList(temp);
    }
    @Override
    public void create() {
        super.create();
        successorsList = new ArrayList<>();
    }

    @Override
    public void join(Node n) throws NodeNotFoundException, FingerTableEmptyException, NetworkFailureException {
        super.join(n);
        updateSuccessorsList();
    }

    public List<Node> getSuccessorsList() {
        return successorsList;
    }

    public void setSuccessorsList(List<Node> successorsList) {
        this.successorsList = successorsList;
    }
}

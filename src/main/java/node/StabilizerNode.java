package node;

import distributedDB.ResourceManager;
import network.exceptions.NetworkFailureException;
import utils.PeriodicActionsManager;

import java.util.function.Consumer;

/**
 * This concrete implementation of {@link LocalNode} add the stabilization methods, as described in the paper.
 * The stabilization methods have no meaning in appearing also in {@link network.remoteNode.RemoteNode}, since these routines are running
 * locally on the instance to which the remote node is pointing. Hence on the {@link StabilizerNode}.
 */
public class StabilizerNode extends LocalNode {

    PeriodicActionsManager periodicActionsManager = new PeriodicActionsManager();

    /**
     * Constructs a Node specifying the periodic actions to execute in order to stabilize it.
     * @param id the node id
     * @param tasks the sequence of actions to execute on a {@link LocalNode}
     * @param labels the labels to assign to each action (w.r.t. to the order of tasks)
     * @param delays the amount of time in milliseconds before scheduling each task (w.r.t. to the order of tasks)
     * @param periods the amount of time in milliseconds among each repetition of the tasks execution
     *                (w.r.t. to the order of tasks)
     */
    public StabilizerNode(int id, Consumer<LocalNode>[] tasks, String[] labels,
                          long[] delays, long[] periods, ResourceManager resourceManager) throws NetworkFailureException {
        super(id, resourceManager);
        periodicActionsManager.initPeriodicActions(this, tasks, labels, delays, periods);
    }

    public StabilizerNode(int id, String host, int port,
                          Consumer<LocalNode>[] tasks, String[] labels,
                          long[] delays, long[] periods, ResourceManager resourceManager) throws NetworkFailureException {
        super(id, host, port, resourceManager);
        periodicActionsManager.initPeriodicActions(this, tasks, labels, delays, periods);
    }

    /**
     * Constructs a Node specifying the periodic actions to execute in order to stabilize it.
     * @param id the node id
     * @param tasks the sequence of actions to execute on a {@link LocalNode}
     * @param labels the labels to assign to each action (w.r.t. to the order of tasks)
     * @param delays the amount of time in milliseconds before scheduling each task (w.r.t. to the order of tasks)
     * @param periods the amount of time in milliseconds among each repetition of the tasks execution
     *                (w.r.t. to the order of tasks)
     */
    public StabilizerNode(int id, Node n, Consumer<LocalNode>[] tasks, String[] labels,
                          long[] delays, long[] periods, ResourceManager resourceManager) throws NetworkFailureException {
        super(id, n, resourceManager);
        periodicActionsManager.initPeriodicActions(this, tasks, labels, delays, periods);
    }

    public StabilizerNode(int id, Node n, String host, int port,
                          Consumer<LocalNode>[] tasks, String[] labels,
                          long[] delays, long[] periods, ResourceManager resourceManager) throws NetworkFailureException {
        super(id, host, port, n, resourceManager);
        periodicActionsManager.initPeriodicActions(this, tasks, labels, delays, periods);
    }

    public void start(){
        periodicActionsManager.start();
    }

    public void stop() {
        System.out.println("This node has been closed!");
        periodicActionsManager.stop();
    }


}

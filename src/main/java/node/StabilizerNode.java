package node;

import network.exceptions.NetworkFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * This concrete implementation of {@link LocalNode} add the stabilization methods, as described in the paper.
 * The stabilization methods have no meaning in appearing also in {@link network.remoteNode.RemoteNode}, since these routines are running
 * locally on the instance to which the remote node is pointing. Hence on the {@link StabilizerNode}.
 */
public class StabilizerNode extends LocalNode {

    private final List<PeriodicAction> periodicActions = new ArrayList<>();
    //private final ExecutorService pool = Executors.newScheduledThreadPool();
    private boolean running;

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
                          long[] delays, long[] periods) {
        super(id);
        initPeriodicActions(tasks, labels, delays, periods);
    }

    public StabilizerNode(int id, String host, int port,
                          Consumer<LocalNode>[] tasks, String[] labels,
                          long[] delays, long[] periods) {
        super(id, host, port);
        initPeriodicActions(tasks, labels, delays, periods);
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
                          long[] delays, long[] periods) throws NetworkFailureException {
        super(id, n);
        initPeriodicActions(tasks, labels, delays, periods);
    }

    public StabilizerNode(int id, Node n, String host, int port,
                          Consumer<LocalNode>[] tasks, String[] labels,
                          long[] delays, long[] periods) throws NetworkFailureException {
        super(id, host, port, n);
        initPeriodicActions(tasks, labels, delays, periods);
    }

    /**
     * Initialises the periodic actions for stabilization
     * @param tasks the sequence of actions to execute on a {@link LocalNode}
     * @param labels the labels to assign to each action (w.r.t. to the order of tasks)
     * @param delays the amount of time in milliseconds before scheduling each task (w.r.t. to the order of tasks)
     * @param periods the amount of time in milliseconds among each repetition of the tasks execution
     */
    private void initPeriodicActions(Consumer<LocalNode>[] tasks, String[] labels,
                                     long[] delays, long[] periods) {
        validate(tasks, labels, delays, periods);

        this.running = false;

        for (int i = 0; i < tasks.length; i++)
            periodicActions.add(new PeriodicAction(this, tasks[i], labels[i],
                    delays[i], periods[i]));
    }

    /**
     * Verifies that all the parameters have same length
     * @param tasks the sequence of actions to execute on a {@link LocalNode}
     * @param labels the labels to assign to each action (w.r.t. to the order of tasks)
     * @param delays the amount of time in milliseconds before scheduling each task (w.r.t. to the order of tasks)
     * @param periods the amount of time in milliseconds among each repetition of the tasks execution
     */
    private void validate(Consumer<LocalNode>[] tasks, String[] labels,
                          long[] delays, long[] periods) {
        if (tasks.length != delays.length ||
                tasks.length != periods.length ||
                tasks.length != labels.length)
            throw new IllegalArgumentException("StabilizerNode(): three arrays' lengths must be the same");
    }

    /**
     * Starts the stabilization mechanisms, by starting every single periodic task
     * (if the server is not running)
     * @see PeriodicAction#start()
     */
    public void start() {
        if(!running) {
            for (PeriodicAction pa : periodicActions)
                pa.start();
            running = true;
        }
    }

    /**
     * If the server is running, stops every periodic action scheduled.
     */
    @Override
    public void close() {
        if(running) {
            for (PeriodicAction pa : periodicActions)
                pa.stop();
            running = false;
        }
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * The motivation of this class is that actually we have several periodic actions,
     * such as stabilize, fix fingers and check predecessor. Others might be added.
     * Then is convenient to have a class that track the timer objects, the frequencies and so on.
     */
    public class PeriodicAction {
        private final TimerTask timerTask;
        private final Timer timer;
        private final long delay;
        private final long period;

        PeriodicAction(StabilizerNode target, Consumer<LocalNode> runnable, String label, long delay, long period) {
            this.timerTask = new TimerTask() {
                @Override
                public void run() {
                    runnable.accept(target);
                }
            };
            this.timer = new Timer(label);
            this.delay = delay;
            this.period = period;
        }

        /**
         * Starts this periodic action
         */
        void start() {
            timer.scheduleAtFixedRate(timerTask, delay, period);
        }

        void stop() {
            timer.cancel();
        }
    }
}

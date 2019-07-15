package utils;

import distributedDB.ResourceManager;
import node.LocalNode;
import node.StabilizerNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class PeriodicActionsManager {

    private final List<PeriodicActionsManager.PeriodicAction> periodicActions = new ArrayList<>();
    //private final ExecutorService pool = Executors.newScheduledThreadPool();
    private boolean running;

    /**
     * Initialises the periodic actions
     * @param tasks the sequence of actions to execute
     * @param labels the labels to assign to each action (w.r.t. to the order of tasks)
     * @param delays the amount of time in milliseconds before scheduling each task (w.r.t. to the order of tasks)
     * @param periods the amount of time in milliseconds among each repetition of the tasks execution
     */
    public <T> void initPeriodicActions(T target, Consumer<T>[] tasks, String[] labels,
                                    long[] delays, long[] periods) {
        validate(tasks, labels, delays, periods);

        this.running = false;

        for (int i = 0; i < tasks.length; i++)
            periodicActions.add(new PeriodicAction(target, tasks[i], labels[i],
                    delays[i], periods[i]));
    }



    /**
     * Verifies that all the parameters have same length
     * @param tasks the sequence of actions to execute
     * @param labels the labels to assign to each action (w.r.t. to the order of tasks)
     * @param delays the amount of time in milliseconds before scheduling each task (w.r.t. to the order of tasks)
     * @param periods the amount of time in milliseconds among each repetition of the tasks execution
     */
    private void validate(Consumer<?>[] tasks, String[] labels,
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
    public void stop() {
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

        <T> PeriodicAction(T target, Consumer<T> runnable, String label, long delay, long period) {
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

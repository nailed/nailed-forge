package jk_5.nailed.scheduler;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.concurrent.scheduler.Task;

/**
 * No description given
 *
 * @author jk-5
 */
class NailedTask implements Task, Runnable {

    private volatile NailedTask next = null;

    /**
     * -1 means no repeating <br>
     * -2 means cancel <br>
     * -3 means processing for Future <br>
     * -4 means done for Future <br>
     * Never 0 <br>
     * >0 means number of ticks to wait between each execution
     */
    private volatile long period;
    private long nextRun;
    private final Runnable task;
    private final int id;

    NailedTask() {
        this(null, -1, -1);
    }

    NailedTask(final Runnable task) {
        this(task, -1, -1);
    }

    NailedTask(final Runnable task, final int id, final long period) {
        this.task = task;
        this.id = id;
        this.period = period;
    }

    @Override
    public final int getTaskId() {
        return id;
    }

    @Override
    public boolean isSync() {
        return true;
    }

    @Override
    public void run() {
        task.run();
    }

    Class<? extends Runnable> getTaskClass() {
        return task.getClass();
    }

    @Override
    public void cancel(){
        NailedAPI.getScheduler().cancelTask(id);
    }

    /**
     * This method properly sets the status to cancelled, synchronizing when required.
     *
     * @return false if it is a craft future task that has already begun execution, true otherwise
     */
    boolean cancel0() {
        setPeriod(-2l);
        return true;
    }

    NailedTask getNext() {
        return this.next;
    }

    long getPeriod() {
        return this.period;
    }

    long getNextRun() {
        return this.nextRun;
    }

    void setNext(NailedTask next) {
        this.next = next;
    }

    void setPeriod(long period) {
        this.period = period;
    }

    void setNextRun(long nextRun) {
        this.nextRun = nextRun;
    }
}

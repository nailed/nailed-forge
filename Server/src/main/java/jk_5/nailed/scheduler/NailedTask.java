package jk_5.nailed.scheduler;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.concurrent.scheduler.Task;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * No description given
 *
 * @author jk-5
 */
class NailedTask implements Task, Runnable {

    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE) private volatile NailedTask next = null;

    /**
     * -1 means no repeating <br>
     * -2 means cancel <br>
     * -3 means processing for Future <br>
     * -4 means done for Future <br>
     * Never 0 <br>
     * >0 means number of ticks to wait between each execution
     */
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE) private volatile long period;
    @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE) private long nextRun;
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

    public final int getTaskId() {
        return id;
    }

    public boolean isSync() {
        return true;
    }

    public void run() {
        task.run();
    }

    Class<? extends Runnable> getTaskClass() {
        return task.getClass();
    }

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
}

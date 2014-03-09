package jk_5.nailed.api.concurrent.scheduler;

import jk_5.nailed.api.NailedAPI;

/**
 * This class is provided as an easy way to handle scheduling tasks.
 *
 * @author jk-5
 */
public abstract class NailedRunnable implements Runnable {

    private int taskId = -1;

    /**
     * Attempts to cancel this task.
     *
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized void cancel() throws IllegalStateException {
        NailedAPI.getScheduler().cancelTask(getTaskId());
    }

    /**
     * Schedules this in the Nailed scheduler to run on next tick.
     *
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see Scheduler#runTask(NailedRunnable)
     */
    public synchronized Task runTask() throws IllegalArgumentException, IllegalStateException {
        checkState();
        return setupId(NailedAPI.getScheduler().runTask(this));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Nailed. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this in the Nailed scheduler to run asynchronously.
     *
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see Scheduler#runTaskAsynchronously(NailedRunnable)
     */
    public synchronized Task runTaskAsynchronously() throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(NailedAPI.getScheduler().runTaskAsynchronously(this));
    }

    /**
     * Schedules this to run after the specified number of server ticks.
     *
     * @param delay the ticks to wait before running the task
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see Scheduler#runTaskLater(NailedRunnable, long)
     */
    public synchronized Task runTaskLater(long delay) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(NailedAPI.getScheduler().runTaskLater(this, delay));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Nailed. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this to run asynchronously after the specified number of
     * server ticks.
     *
     * @param delay the ticks to wait before running the task
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see Scheduler#runTaskLaterAsynchronously(NailedRunnable, long)
     */
    public synchronized Task runTaskLaterAsynchronously(long delay) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(NailedAPI.getScheduler().runTaskLaterAsynchronously(this, delay));
    }

    /**
     * Schedules this to repeatedly run until cancelled, starting after the
     * specified number of server ticks.
     *
     * @param delay the ticks to wait before running the task
     * @param period the ticks to wait between runs
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see Scheduler#runTaskTimer(NailedRunnable, long, long)
     */
    public synchronized Task runTaskTimer(long delay, long period) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(NailedAPI.getScheduler().runTaskTimer(this, delay, period));
    }

    /**
     * <b>Asynchronous tasks should never access any API in Nailed. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Schedules this to repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param delay the ticks to wait before running the task for the first
     *     time
     * @param period the ticks to wait between runs
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if plugin is null
     * @throws IllegalStateException if this was already scheduled
     * @see Scheduler#runTaskTimerAsynchronously(NailedRunnable, long, long)
     */
    public synchronized Task runTaskTimerAsynchronously(long delay, long period) throws IllegalArgumentException, IllegalStateException  {
        checkState();
        return setupId(NailedAPI.getScheduler().runTaskTimerAsynchronously(this, delay, period));
    }

    /**
     * Gets the task id for this runnable.
     *
     * @return the task id that this runnable was scheduled as
     * @throws IllegalStateException if task was not scheduled yet
     */
    public synchronized int getTaskId() throws IllegalStateException {
        final int id = taskId;
        if(id == -1){
            throw new IllegalStateException("Not scheduled yet");
        }
        return id;
    }

    private void checkState() {
        if(taskId != -1){
            throw new IllegalStateException("Already scheduled as " + taskId);
        }
    }

    private Task setupId(final Task task) {
        this.taskId = task.getTaskId();
        return task;
    }
}

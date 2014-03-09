package jk_5.nailed.api.concurrent.scheduler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface Scheduler {

    /**
     * Schedules a once off task to occur after a delay.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param task Task to be executed
     * @param delay Delay in server ticks before executing task
     * @return Task id number (-1 if scheduling failed)
     */
    public int scheduleSyncDelayedTask(NailedRunnable task, long delay);

    /**
     * Schedules a once off task to occur as soon as possible.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param task Task to be executed
     * @return Task id number (-1 if scheduling failed)
     */
    public int scheduleSyncDelayedTask(NailedRunnable task);

    /**
     * Schedules a repeating task.
     * <p>
     * This task will be executed by the main server thread.
     *
     * @param task Task to be executed
     * @param delay Delay in server ticks before executing first repeat
     * @param period Period in server ticks of the task
     * @return Task id number (-1 if scheduling failed)
     */
    public int scheduleSyncRepeatingTask(NailedRunnable task, long delay, long period);

    /**
     * Calls a method on the main thread and returns a Future object. This
     * task will be executed by the main server thread.
     * <ul>
     * <li>Note: The Future.get() methods must NOT be called from the main
     *     thread.
     * <li>Note2: There is at least an average of 10ms latency until the
     *     isDone() method returns true.
     * </ul>
     * @param <T> The callable's return type
     * @param task Task to be executed
     * @return Future Future object related to the task
     */
    public <T> Future<T> callSyncMethod(Callable<T> task);

    /**
     * Removes task from scheduler.
     *
     * @param taskId Id number of task to be removed
     */
    public void cancelTask(int taskId);

    /**
     * Removes all tasks from the scheduler.
     */
    public void cancelAllTasks();

    /**
     * Check if the task currently running.
     * <p>
     * A repeating task might not be running currently, but will be running in
     * the future. A task that has finished, and does not repeat, will not be
     * running ever again.
     * <p>
     * Explicitly, a task is running if there exists a thread for it, and that
     * thread is alive.
     *
     * @param taskId The task to check.
     * <p>
     * @return If the task is currently running.
     */
    public boolean isCurrentlyRunning(int taskId);

    /**
     * Check if the task queued to be run later.
     * <p>
     * If a repeating task is currently running, it might not be queued now
     * but could be in the future. A task that is not queued, and not running,
     * will not be queued again.
     *
     * @param taskId The task to check.
     * <p>
     * @return If the task is queued to be run.
     */
    public boolean isQueued(int taskId);

    /**
     * Returns a list of all active workers.
     * <p>
     * This list contains asynch tasks that are being executed by separate
     * threads.
     *
     * @return Active workers
     */
    public List<Worker> getActiveWorkers();

    /**
     * Returns a list of all pending tasks. The ordering of the tasks is not
     * related to their order of execution.
     *
     * @return Active workers
     */
    public List<Task> getPendingTasks();

    /**
     * Returns a task that will run on the next server tick.
     *
     * @param task the task to be run
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if task is null
     */
    public Task runTask(NailedRunnable task) throws IllegalArgumentException;

    /**
     * <b>Asynchronous tasks should never access any API in Minecraft. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Returns a task that will run asynchronously.
     *
     * @param task the task to be run
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if task is null
     */
    public Task runTaskAsynchronously(NailedRunnable task) throws IllegalArgumentException;

    /**
     * Returns a task that will run after the specified number of server
     * ticks.
     *
     * @param task the task to be run
     * @param delay the ticks to wait before running the task
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if task is null
     */
    public Task runTaskLater(NailedRunnable task, long delay) throws IllegalArgumentException;

    /**
     * <b>Asynchronous tasks should never access any API in Minecraft. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Returns a task that will run asynchronously after the specified number
     * of server ticks.
     *
     * @param task the task to be run
     * @param delay the ticks to wait before running the task
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if task is null
     */
    public Task runTaskLaterAsynchronously(NailedRunnable task, long delay) throws IllegalArgumentException;

    /**
     * Returns a task that will repeatedly run until cancelled, starting after
     * the specified number of server ticks.
     *
     * @param task the task to be run
     * @param delay the ticks to wait before running the task
     * @param period the ticks to wait between runs
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if task is null
     */
    public Task runTaskTimer(NailedRunnable task, long delay, long period) throws IllegalArgumentException;

    /**
     * <b>Asynchronous tasks should never access any API in Minecraft. Great care
     * should be taken to assure the thread-safety of asynchronous tasks.</b>
     * <p>
     * Returns a task that will repeatedly run asynchronously until cancelled,
     * starting after the specified number of server ticks.
     *
     * @param task the task to be run
     * @param delay the ticks to wait before running the task for the first
     *     time
     * @param period the ticks to wait between runs
     * @return a Task that contains the id number
     * @throws IllegalArgumentException if task is null
     */
    public Task runTaskTimerAsynchronously(NailedRunnable task, long delay, long period) throws IllegalArgumentException;
}

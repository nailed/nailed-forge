package jk_5.nailed.api.concurrent.scheduler;

/**
 * Represents a task being executed by the scheduler
 */
public interface Task {

    /**
     * Returns the taskId for the task.
     *
     * @return Task id number
     */
    int getTaskId();

    /**
     * Returns true if the Task is a sync task.
     *
     * @return true if the task is run by main thread
     */
    boolean isSync();

    /**
     * Will attempt to cancel this task.
     */
    void cancel();
}

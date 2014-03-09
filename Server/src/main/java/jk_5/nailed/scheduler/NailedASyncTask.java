package jk_5.nailed.scheduler;

import jk_5.nailed.api.concurrent.scheduler.Worker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
class NailedASyncTask extends NailedTask {

    private final LinkedList<Worker> workers = new LinkedList<Worker>();
    private final Map<Integer, NailedTask> runners;

    NailedASyncTask(final Map<Integer, NailedTask> runners, final Runnable task, final int id, final long delay) {
        super(task, id, delay);
        this.runners = runners;
    }

    @Override
    public boolean isSync() {
        return false;
    }

    @Override
    public void run() {
        final Thread thread = Thread.currentThread();
        synchronized(workers) {
            if (getPeriod() == -2) {
                // Never continue running after cancelled.
                // Checking this with the lock is important!
                return;
            }
            workers.add(
                    new Worker() {
                        public Thread getThread() {
                            return thread;
                        }

                        public int getTaskId() {
                            return NailedASyncTask.this.getTaskId();
                        }
                    });
        }
        Throwable thrown = null;
        try {
            super.run();
        } catch (final Throwable t) {
            thrown = t;
            throw new RuntimeException(String.format("Exception while executing task %s", getTaskId()), thrown);
        } finally {
            // Cleanup is important for any async task, otherwise ghost tasks are everywhere
            synchronized(workers) {
                try {
                    final Iterator<Worker> workers = this.workers.iterator();
                    boolean removed = false;
                    while (workers.hasNext()) {
                        if (workers.next().getThread() == thread) {
                            workers.remove();
                            removed = true; // Don't throw exception
                            break;
                        }
                    }
                    if (!removed) {
                        throw new IllegalStateException(String.format("Unable to remove worker %s on task %s",thread.getName(),getTaskId()),thrown); // We don't want to lose the original exception, if any
                    }
                } finally {
                    if (getPeriod() < 0 && workers.isEmpty()) {
                        // At this spot, we know we are the final async task being executed!
                        // Because we have the lock, nothing else is running or will run because delay < 0
                        runners.remove(getTaskId());
                    }
                }
            }
        }
    }

    LinkedList<Worker> getWorkers() {
        return workers;
    }

    boolean cancel0() {
        synchronized (workers) {
            // Synchronizing here prevents race condition for a completing task
            setPeriod(-2l);
            if (workers.isEmpty()) {
                runners.remove(getTaskId());
            }
        }
        return true;
    }
}

package jk_5.nailed.scheduler;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import javax.annotation.*;

import com.google.common.util.concurrent.*;

import org.apache.commons.lang3.*;

import net.minecraft.server.*;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.*;

import jk_5.nailed.*;
import jk_5.nailed.api.concurrent.scheduler.*;

/**
 * The fundamental concepts for this implementation:
 * <li>Main thread owns {@link #head} and {@link #currentTick}, but it may be read from any thread</li>
 * <li>Main thread exclusively controls {@link #temp} and {@link #pending}.
 * They are never to be accessed outside of the main thread; alternatives exist to prevent locking.</li>
 * <li>{@link #head} to {@link #tail} act as a linked list/queue, with 1 consumer and infinite producers.
 * Adding to the tail is atomic and very efficient; utility method is {@link #handle(NailedTask, long)} or {@link #addTask(NailedTask)}. </li>
 * <li>Changing the period on a task is delicate.
 * Any future task needs to notify waiting threads.
 * Async tasks must be synchronized to make sure that any thread that's finishing will remove itself from {@link #runners}.
 * <li>{@link #runners} provides a moderately up-to-date view of active tasks.
 * If the linked head to tail set is read, all remaining tasks that were active at the time execution started will be located in runners.</li>
 * <li>Async tasks are responsible for removing themselves from runners</li>
 * <li>Sync tasks are only to be removed from runners on the main thread when coupled with a removal from pending and temp.</li>
 * <li>Most of the design in this scheduler relies on queuing special tasks to perform any data changes on the main thread.
 * When executed from inside a synchronous method, the scheduler will be updated before next execution by virtue of the frequent {@link #parsePending()} calls.</li>
 */
public class NailedScheduler implements Scheduler {

    private static final int RECENT_TICKS;

    /**
     * Counter for IDs. Order doesn't matter, only uniqueness.
     */
    private final AtomicInteger ids = new AtomicInteger(1);
    /**
     * Current head of linked-list. This reference is always stale, {@link NailedTask#next} is the live reference.
     */
    private volatile NailedTask head = new NailedTask();
    /**
     * Tail of a linked-list. AtomicReference only matters when adding to queue
     */
    private final AtomicReference<NailedTask> tail = new AtomicReference<NailedTask>(head);
    /**
     * Main thread logic only
     */
    private final PriorityQueue<NailedTask> pending = new PriorityQueue<NailedTask>(10,
            new Comparator<NailedTask>() {
                @Override
                public int compare(final NailedTask o1, final NailedTask o2) {
                    return (int) (o1.getNextRun() - o2.getNextRun());
                }
            }
    );
    /**
     * Main thread logic only
     */
    private final List<NailedTask> temp = new ArrayList<NailedTask>();
    /**
     * These are tasks that are currently active. It's provided for 'viewing' the current state.
     */
    private final ConcurrentHashMap<Integer, NailedTask> runners = new ConcurrentHashMap<Integer, NailedTask>();
    private volatile int currentTick = -1;
    private final Executor executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("NailedSchedulerWorker-%d").build());
    private NailedASyncDebugger debugHead = new NailedASyncDebugger(-1, null) {
        @Override
        StringBuilder debugTo(StringBuilder string) {
            return string;
        }
    };
    private NailedASyncDebugger debugTail = debugHead;

    static {
        RECENT_TICKS = 30;
    }

    @Override
    public int scheduleSyncDelayedTask(final NailedRunnable task) {
        return this.scheduleSyncDelayedTask(task, 0L);
    }

    @Override
    public Task runTask(NailedRunnable runnable) {
        return runTaskLater(runnable, 0L);
    }

    @Override
    public Task runTaskAsynchronously(NailedRunnable runnable) {
        return runTaskLaterAsynchronously(runnable, 0L);
    }

    @Override
    public int scheduleSyncDelayedTask(final NailedRunnable task, final long delay) {
        return this.scheduleSyncRepeatingTask(task, delay, -1L);
    }

    @Override
    public Task runTaskLater(NailedRunnable runnable, long delay) {
        return runTaskTimer(runnable, delay, -1L);
    }

    @Override
    public Task runTaskLaterAsynchronously(NailedRunnable runnable, long delay) {
        return runTaskTimerAsynchronously(runnable, delay, -1L);
    }

    @Override
    public int scheduleSyncRepeatingTask(final NailedRunnable runnable, long delay, long period) {
        return runTaskTimer(runnable, delay, period).getTaskId();
    }

    @Override
    public Task runTaskTimer(NailedRunnable runnable, long delay, long period) {
        Validate.notNull(runnable, "Task cannot be null");
        if(delay < 0L){
            delay = 0;
        }
        if(period == 0L){
            period = 1L;
        }else if(period < -1L){
            period = -1L;
        }
        return handle(new NailedTask(runnable, nextId(), period), delay);
    }

    @Override
    public Task runTaskTimerAsynchronously(NailedRunnable runnable, long delay, long period) {
        Validate.notNull(runnable, "Task cannot be null");
        if(delay < 0L){
            delay = 0;
        }
        if(period == 0L){
            period = 1L;
        }else if(period < -1L){
            period = -1L;
        }
        return handle(new NailedASyncTask(runners, runnable, nextId(), period), delay);
    }

    @Override
    public <T> Future<T> callSyncMethod(final Callable<T> task) {
        Validate.notNull(task, "Task cannot be null");
        final NailedFuture<T> future = new NailedFuture<T>(task, nextId());
        handle(future, 0L);
        return future;
    }

    @Override
    public void cancelTask(final int taskId) {
        if(taskId <= 0){
            return;
        }
        NailedTask task = runners.get(taskId);
        if(task != null){
            task.cancel0();
        }
        task = new NailedTask(
                new NailedRunnable() {
                    @Override
                    public void run() {
                        if(!check(NailedScheduler.this.temp)){
                            check(NailedScheduler.this.pending);
                        }
                    }

                    private boolean check(final Iterable<NailedTask> collection) {
                        final Iterator<NailedTask> tasks = collection.iterator();
                        while(tasks.hasNext()){
                            final NailedTask task = tasks.next();
                            if(task.getTaskId() == taskId){
                                task.cancel0();
                                tasks.remove();
                                if(task.isSync()){
                                    runners.remove(taskId);
                                }
                                return true;
                            }
                        }
                        return false;
                    }
                }
        );
        handle(task, 0L);
        for(NailedTask taskPending = head.getNext(); taskPending != null; taskPending = taskPending.getNext()){
            if(taskPending == task){
                return;
            }
            if(taskPending.getTaskId() == taskId){
                taskPending.cancel0();
            }
        }
    }

    @Override
    public void cancelAllTasks() {
        final NailedTask task = new NailedTask(
                new NailedRunnable() {
                    @Override
                    public void run() {
                        Iterator<NailedTask> it = NailedScheduler.this.runners.values().iterator();
                        while(it.hasNext()){
                            NailedTask task = it.next();
                            task.cancel0();
                            if(task.isSync()){
                                it.remove();
                            }
                        }
                        NailedScheduler.this.pending.clear();
                        NailedScheduler.this.temp.clear();
                    }
                }
        );
        handle(task, 0L);
        for(NailedTask taskPending = head.getNext(); taskPending != null; taskPending = taskPending.getNext()){
            if(taskPending == task){
                break;
            }
            taskPending.cancel0();
        }
        for(NailedTask runner : runners.values()){
            runner.cancel0();
        }
    }

    @Override
    public boolean isCurrentlyRunning(final int taskId) {
        final NailedTask task = runners.get(taskId);
        if(task == null || task.isSync()){
            return false;
        }
        final NailedASyncTask asyncTask = (NailedASyncTask) task;
        synchronized(asyncTask.getWorkers()){
            return asyncTask.getWorkers().isEmpty();
        }
    }

    @Override
    public boolean isQueued(final int taskId) {
        if(taskId <= 0){
            return false;
        }
        for(NailedTask task = head.getNext(); task != null; task = task.getNext()){
            if(task.getTaskId() == taskId){
                return task.getPeriod() >= -1L; // The task will run
            }
        }
        NailedTask task = runners.get(taskId);
        return task != null && task.getPeriod() >= -1L;
    }

    @Override
    public List<Worker> getActiveWorkers() {
        final ArrayList<Worker> workers = new ArrayList<Worker>();
        for(final NailedTask taskObj : runners.values()){
            // Iterator will be a best-effort (may fail to grab very new values) if called from an async thread
            if(taskObj.isSync()){
                continue;
            }
            final NailedASyncTask task = (NailedASyncTask) taskObj;
            synchronized(task.getWorkers()){
                // This will never have an issue with stale threads; it's state-safe
                workers.addAll(task.getWorkers());
            }
        }
        return workers;
    }

    @Override
    public List<Task> getPendingTasks() {
        final ArrayList<NailedTask> truePending = new ArrayList<NailedTask>();
        for(NailedTask task = head.getNext(); task != null; task = task.getNext()){
            if(task.getTaskId() != -1){
                // -1 is special code
                truePending.add(task);
            }
        }

        final ArrayList<Task> pending = new ArrayList<Task>();
        for(NailedTask task : runners.values()){
            if(task.getPeriod() >= -1L){
                pending.add(task);
            }
        }

        for(final NailedTask task : truePending){
            if(task.getPeriod() >= -1L && !pending.contains(task)){
                pending.add(task);
            }
        }
        return pending;
    }

    /**
     * This method is designed to never block or wait for locks; an immediate execution of all current tasks.
     */
    @SubscribeEvent
    public void mainThreadHeartbeat(TickEvent.ServerTickEvent event) {
        if(event.phase == TickEvent.Phase.END){
            return;
        }
        final int currentTick = MinecraftServer.getServer().getTickCounter();
        this.currentTick = currentTick;
        final List<NailedTask> temp = this.temp;
        parsePending();
        while(isReady(currentTick)){
            final NailedTask task = pending.remove();
            if(task.getPeriod() < -1L){
                if(task.isSync()){
                    runners.remove(task.getTaskId(), task);
                }
                parsePending();
                continue;
            }
            if(task.isSync()){
                try{
                    task.run();
                }catch(final Throwable throwable){
                    NailedLog.warn("Task {} generated an exception", task.getTaskId());
                    NailedLog.warn("Exception: ", throwable);
                }
                parsePending();
            }else{
                debugTail = debugTail.setNext(new NailedASyncDebugger(currentTick + RECENT_TICKS, task.getTaskClass()));
                executor.execute(task);
                // We don't need to parse pending
                // (async tasks must live with race-conditions if they attempt to cancel between these few lines of code)
            }
            final long period = task.getPeriod(); // State consistency
            if(period > 0){
                task.setNextRun(currentTick + period);
                temp.add(task);
            }else if(task.isSync()){
                runners.remove(task.getTaskId());
            }
        }
        pending.addAll(temp);
        temp.clear();
        debugHead = debugHead.getNextHead(currentTick);
    }

    private void addTask(final NailedTask task) {
        final AtomicReference<NailedTask> tail = this.tail;
        NailedTask tailTask = tail.get();
        while(!tail.compareAndSet(tailTask, task)){
            tailTask = tail.get();
        }
        tailTask.setNext(task);
    }

    private NailedTask handle(final NailedTask task, final long delay) {
        task.setNextRun(currentTick + delay);
        addTask(task);
        return task;
    }

    private int nextId() {
        return ids.incrementAndGet();
    }

    private void parsePending() {
        NailedTask head = this.head;
        NailedTask task = head.getNext();
        NailedTask lastTask = head;
        for(; task != null; task = (lastTask = task).getNext()){
            if(task.getTaskId() == -1){
                task.run();
            }else if(task.getPeriod() >= -1L){
                pending.add(task);
                runners.put(task.getTaskId(), task);
            }
        }
        // We split this because of the way things are ordered for all of the async calls in NailedScheduler
        // (it prevents race-conditions)
        for(task = head; task != lastTask; task = head){
            head = task.getNext();
            task.setNext(null);
        }
        this.head = lastTask;
    }

    private boolean isReady(final int currentTick) {
        return !pending.isEmpty() && pending.peek().getNextRun() <= currentTick;
    }

    @Override
    public String toString() {
        int debugTick = currentTick;
        StringBuilder string = new StringBuilder("Recent tasks from ").append(debugTick - RECENT_TICKS).append('-').append(debugTick).append('{');
        debugHead.debugTo(string);
        return string.append('}').toString();
    }

    @Override
    public void execute(@Nonnull final Runnable command) {
        this.runTaskAsynchronously(new NailedRunnable() {
            @Override
            public void run() {
                command.run();
            }
        });
    }
}

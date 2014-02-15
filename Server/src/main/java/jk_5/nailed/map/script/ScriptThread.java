package jk_5.nailed.map.script;

import com.google.common.collect.Lists;
import jk_5.nailed.NailedLog;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * No description given
 *
 * @author jk-5
 */
public class ScriptThread {

    private static final Object lock = new Object();
    private static final Object monitor = new Object();
    private static final Object defaultQueue = new Object();
    private static Thread thread;
    private static boolean running = false;
    private static boolean stopped = false;
    private static boolean busy = false;

    private static List<LinkedBlockingQueue<Task>> pendingTasks = Lists.newArrayList();
    private static List<LinkedBlockingQueue<Task>> activeTasks = Lists.newArrayList();
    private static Map<Object, LinkedBlockingQueue<Task>> machineTasks = new WeakHashMap<Object, LinkedBlockingQueue<Task>>();

    public static void start(){
        synchronized(lock){
            if(running){
                stopped = false;
                return;
            }

            thread = new Thread(new Runnable() {

                @Override
                public void run(){
                    while(true){
                        synchronized(pendingTasks){
                            if(!pendingTasks.isEmpty()){
                                Iterator<LinkedBlockingQueue<Task>> it = pendingTasks.iterator();
                                if(it.hasNext()){
                                    LinkedBlockingQueue<Task> queue = it.next();
                                    if(!activeTasks.contains(queue)){
                                        activeTasks.add(queue);
                                    }
                                    it.remove();
                                    continue;
                                }
                            }
                        }

                        Iterator<LinkedBlockingQueue<Task>> it = activeTasks.iterator();
                        while(it.hasNext()){
                            LinkedBlockingQueue<Task> queue = it.next();
                            if(queue != null && !queue.isEmpty()){
                                synchronized(lock){
                                    if(stopped){
                                        running = false; //FIXME
                                        thread = null; //FIXME
                                        return;
                                    }
                                }

                                try{
                                    final Task task = queue.take();
                                    busy = true; //FIXME
                                    Thread worker = new Thread(){
                                        @Override
                                        public void run(){
                                            try{
                                                task.execute();
                                            }catch(Throwable e){
                                                NailedLog.error(e, "Error running task");
                                            }
                                        }
                                    };
                                    worker.start();
                                    worker.join(5000);
                                    if(worker.isAlive()){
                                        ScriptingMachine owner = task.getOwner();
                                        if(owner != null){
                                            owner.abort(false);
                                            worker.join(1250);
                                            if(worker.isAlive()){
                                                owner.abort(true);
                                                worker.join(1250);
                                            }
                                        }
                                        if(worker.isAlive()){
                                            worker.interrupt();
                                            worker.stop();
                                        }
                                    }
                                }catch(InterruptedException e){
                                    busy = false;
                                    continue;
                                }finally{
                                    busy = false;
                                }
                                synchronized(queue){
                                    if(queue.isEmpty()){
                                        it.remove();
                                    }
                                }
                            }
                        }
                        while(activeTasks.isEmpty() && pendingTasks.isEmpty()){
                            synchronized(monitor){
                                try{
                                    monitor.wait();
                                }catch(InterruptedException e){
                                    //Just ignore this. Shouldn't happen
                                }
                            }
                        }
                    }
                }
            });
            thread.start();
            running = true;
        }
    }

    public static void stop(){
        synchronized(lock){
            if(running){
                stopped = true;
                thread.interrupt();
            }
        }
    }

    public static void queueTask(Task task, ScriptingMachine owner){
        Object queueObject = owner;
        if(queueObject == null){
            queueObject = defaultQueue;
        }
        LinkedBlockingQueue<Task> queue = machineTasks.get(queueObject);
        if(queue == null){
            machineTasks.put(queueObject, queue = new LinkedBlockingQueue<Task>(256));
        }
        synchronized(pendingTasks){
            boolean added = queue.offer(task);
            if(!pendingTasks.contains(queue)){
                pendingTasks.add(queue);
            }
        }
        synchronized(monitor){
            monitor.notify();
        }
    }

    public interface Task {
        public ScriptingMachine getOwner();
        public void execute();
    }
}

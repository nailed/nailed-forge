package jk_5.nailed.map.script;

import java.util.concurrent.atomic.*;

import gnu.trove.map.hash.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class MachineRegistry<T> {

    private static final AtomicInteger nextMachineId = new AtomicInteger(0);
    private final TIntObjectHashMap<Entry> machines = new TIntObjectHashMap<Entry>();

    public int getUnusedInstanceID() {
        int i = 0;
        while(this.machines.containsKey(i)){
            i++;
        }
        return i;
    }

    public T get(int id) {
        if(this.machines.containsKey(id)){
            return this.machines.get(id).machine;
        }
        return null;
    }

    public void add(int id, T machine) {
        if(id >= 0 && !this.machines.containsKey(id)){
            this.machines.put(id, new Entry(machine));
        }
    }

    public void remove(int id) {
        if(this.machines.containsKey(id)){
            this.machines.remove(id);
        }
    }

    public void reset() {
        this.machines.clear();
    }

    private class Entry {

        public final T machine;

        public Entry(T machine) {
            this.machine = machine;
        }
    }

    public static int getNextId() {
        return nextMachineId.getAndIncrement();
    }
}

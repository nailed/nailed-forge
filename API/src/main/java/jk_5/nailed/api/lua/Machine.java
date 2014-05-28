package jk_5.nailed.api.lua;

import java.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Machine extends Context {

    /**
     * The underlying architecture of the machine.
     * <p/>
     * This is what actually evaluates code running on the machine, where the
     * machine class itself serves as a scheduler.
     *
     * @return the architecture of this machine.
     */
    Architecture architecture();

    /**
     * The list of components attached to this machine.
     * <p/>
     * This maps address to component type/name. Note that the list may not
     * immediately reflect changes after components were added to the network,
     * since such changes are cached in an internal list of 'added components'
     * that are processed in the machine's update logic (i.e. server tick).
     * <p/>
     * This list is kept up-to-date automatically, do <em>not</em> mess with it.
     *
     * @return the list of attached components.
     */
    Set<Component> components();

    /**
     * The address of the file system that holds the machine's read only data
     * (rom). This file system is populated based on the backing resource file
     * systems specified for the machines architecture via
     * {@link Machine#addRomResource(Class, java.util.concurrent.Callable, String)}.
     * This may return <tt>null</tt> if the creation of the file system
     * failed.
     * <p/>
     * Use this in a custom architecture to allow code do differentiate the
     * tmpfs from other file systems, for example.
     *
     * @return the address of the rom component, or <tt>null</tt>.
     */
    String romAddress();

    /**
     * A string with the last error message.
     * <p/>
     * The error string is set either when the machine crashes (see the
     * {@link #crash(String)} method), or when it fails to start (which,
     * technically, is also a crash).
     * <p/>
     * When the machine started, this is reset to <tt>null</tt>.
     *
     * @return the last error message, or <tt>null</tt>.
     */
    String lastError();

    /**
     * The time that has passed since the machine was started, in seconds.
     * <p/>
     * Note that this is actually measured in world time, so the resolution is
     * pretty limited. This is done to avoid 'time skips' when leaving the game
     * and coming back later, resuming a persisted machine.
     */
    double upTime();

    /**
     * The time spent running the underlying architecture in execution threads,
     * i.e. the time spent in {@link Architecture#runThreaded(boolean)} since
     * the machine was last started, in seconds.
     */
    double cpuTime();

    /**
     * Crashes the computer.
     * <p/>
     * This is exactly the same as {@link Context#stop()}, except that it also
     * sets the error message in the machine. This message can be seen when the
     * Analyzer is used on computer cases, for example.
     *
     * @param message the message to set.
     * @return <tt>true</tt> if the computer switched to the stopping state.
     */
    boolean crash(String message);

    /**
     * Tries to pop a signal from the queue and returns it.
     * <p/>
     * Signals are stored in a FIFO queue of limited size. This method is / must
     * be called by architectures regularly to process the queue.
     *
     * @return a signal or <tt>null</tt> if the queue was empty.
     */
    Signal popSignal();

    /**
     * Makes the machine call a component callback.
     * <p/>
     * This is intended to be used from architectures, but may be useful in
     * other scenarios, too. It will make the machine call the method with the
     * specified name on the attached component with the specified address.
     * <p/>
     * This will perform a visibility check, ensuring the component can be seen
     * from the machine. It will also ensure that the direct call limit for
     * individual callbacks is respected.
     *
     * @param address the address of the component to call the method on.
     * @param method  the name of the method to call.
     * @param args    the list of arguments to pass to the callback.
     * @return a list of results returned by the callback, or <tt>null</tt>.
     * @throws LimitReachedException    when the called method supports direct
     *                                  calling, but the number of calls in this
     *                                  tick has exceeded the allowed limit.
     * @throws IllegalArgumentException if there is no such component.
     * @throws Exception                if the callback throws an exception.
     */
    Object[] invoke(String address, String method, Object[] args) throws Exception;

    /**
     * Retrieves the docstring for the specified method of the specified
     * component. This is the string set in a method's {@link LuaMethod}
     * annotation.
     *
     * @param address the address of the component.
     * @param method  the name of the method.
     * @return the docstring for that method.
     */
    String documentation(String address, String method);

    jk_5.nailed.api.map.Map map();
}

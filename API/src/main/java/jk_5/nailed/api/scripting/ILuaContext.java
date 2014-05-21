package jk_5.nailed.api.scripting;

import org.luaj.vm2.LuaValue;

/**
 * An interface passed to machines and ILuaObjects' by machines, providing methods
 * that allow the api call to wait for events before returning, just like in lua.
 * This is very useful if you need to signal work to be performed on the main thread, and don't want to return
 * until the work has been completed.
 */
public interface ILuaContext{

    /**
     * Wait for an event to occur on the machine, suspending the thread until it arises. This method is exactly equivalent to os.pullEvent() in lua.
     * @param filter A specific event to wait for, or null to wait for any event
     * @return An object array containing the name of the event that occurred, and any event parameters
     * @throws Exception If the user presses CTRL+T to terminate the current program while pullEvent() is waiting for an event, a "Terminated" exception will be thrown here.
     * Do not attempt to block this exception, unless you wish to prevent termination, which is not recommended.
     * @throws InterruptedException If the user shuts down or reboots the machine while pullEvent() is waiting for an event, InterruptedException will be thrown. This exception must not be caught or intercepted, or the machine will leak memory and end up in a broken state.
     */
    public Object[] pullEvent(String filter) throws Exception, InterruptedException;

    /**
     * The same as pullEvent(), except "terminated" events are ignored. Only use this if you want to prevent program termination, which is not recommended. This method is exactly equivalent to os.pullEventRaw() in lua.
     * @param filter A specific event to wait for, or null to wait for any event
     * @return An object array containing the name of the event that occurred, and any event parameters
     * @throws InterruptedException If the user shuts down or reboots the machine while pullEventRaw() is waiting for an event, InterruptedException will be thrown. This exception must not be caught or intercepted, or the machine will leak memory and end up in a broken state.
     * @see #pullEvent(String)
     */
    public Object[] pullEventRaw(String filter) throws InterruptedException;

    /**
     * Yield the current coroutine with some arguments until it is resumed. This method is exactly equivalent to coroutine.yield() in lua. Use pullEvent() if you wish to wait for events.
     * @param arguments An object array containing the arguments to pass to coroutine.yield()
     * @return An object array containing the return values from coroutine.yield()
     * @throws InterruptedException If the user shuts down or reboots the machine the coroutine is suspended, InterruptedException will be thrown. This exception must not be caught or intercepted, or the machine will leak memory and end up in a broken state.
     * @see #pullEvent(String)
     */
    public Object[] yield(Object[] arguments) throws InterruptedException;

    public LuaValue[] toValues(Object[] objects, int leaveEmpty);
}

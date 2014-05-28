package jk_5.nailed.api.lua;

/**
 * This interface abstracts away any language specific details for the Machine.
 * <p/>
 * This allows the introduction of other languages, e.g. computers that run
 * assembly or some other language interpreter. The two architectures included
 * in Nailed are the native Lua architecture (using native LuaC) and the
 * Java Lua architecture (using LuaJ).
 *
 * @author jk-5
 */
public interface Architecture {

    /**
     * A display-friendly name of the architecture.
     */
    String name();

    /**
     * Used to check if the machine is fully initialized.
     * This is used to check whether limits on direct calls should be
     * enforced or not - this allows a quick boot phase in the language's
     * kernel logic before switching to business-as-usual.
     *
     * @return whether the machine is fully initialized.
     */
    boolean isInitialized();

    /**
     * Called when a machine starts up. Used to (re-)initialize the underlying
     * architecture logic. For example, for Lua this creates a new Lua state.
     * <p/>
     * This also sets up any built-in APIs for the underlying language, such as
     * querying available memory, listing and interacting with components and so
     * on. If this returns <tt>false</tt> the machine fails to start.
     *
     * @return whether the architecture was initialized successfully.
     */
    boolean initialize();

    /**
     * Called when a machine stopped. Used to clean up any handles, memory and
     * so on. For example, for Lua this destroys the Lua state.
     */
    void close();

    /**
     * Performs a synchronized call initialized in a previous call to
     * {@link #runThreaded(boolean)}.
     * <p/>
     * This method is invoked from the main server thread, meaning it is safe
     * to interact with the world without having to perform manual
     * synchronization.
     * <p/>
     * This method is expected to leave the architecture in a state so it is
     * prepared to next be called with <tt>runThreaded(true)</tt>. For example,
     * the Lua architecture will leave the results of the synchronized call on
     * the stack so they can be further processed in the next call to
     * <tt>runThreaded</tt>.
     */
    void runSynchronized();

    /**
     * Continues execution of the machine. The first call may be used to
     * initialize the machine (e.g. for Lua we load the libraries in the first
     * call so that the computers boot faster). After that the architecture
     * <em>should</em> return <tt>true</tt> from {@link #isInitialized()}.
     * <p/>
     * The resumed state is either a return from a synchronized call, when a
     * synchronized call has been completed (via <tt>runSynchronized</tt>), or
     * a normal yield in all other cases (sleep, interrupt, boot, ...).
     * <p/>
     * This is expected to return within a very short time, usually. For example,
     * in Lua this returns as soon as the state yields, and returns at the latest
     * when the Settings.timeout is reached (in which case it forces the state
     * to crash).
     * <p/>
     * This is expected to consume a single signal if one is present and return.
     * If returning from a synchronized call this should consume no signal.
     *
     * @param isSynchronizedReturn whether the architecture is resumed from an
     *                             earlier synchronized call. In the case of
     *                             Lua this means the results of the call are
     *                             now on the stack, for example.
     * @return the result of the execution. Used to determine the new state.
     */
    ExecutionResult runThreaded(boolean isSynchronizedReturn);
}

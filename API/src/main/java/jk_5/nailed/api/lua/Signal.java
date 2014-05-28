package jk_5.nailed.api.lua;

/**
 * A single signal that was queued on a machine.
 * <p/>
 * This interface is not intended to be implemented, it only serves as a return
 * type for {@link Machine#popSignal()}.
 *
 * @author jk-5
 */
public interface Signal {

    /**
     * The name of the signal.
     */
    String name();

    /**
     * The list of arguments for the signal.
     */
    Object[] args();
}

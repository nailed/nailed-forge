package jk_5.nailed.api.lua;

/**
 * No description given
 *
 * @author jk-5
 */
public interface Component {

    /**
     * The list of names of methods exposed by this component.
     */
    Iterable<String> methods();

    /**
     * Tries to call a function with the specified name on this component.
     * <p/>
     * The name of the method must be one of the names in {@link #methods()}.
     * The returned array may be <tt>null</tt> if there is no return value.
     *
     * @param method    the name of the method to call.
     * @param context   the context from which the method is called, usually the
     *                  instance of the computer running the script that made
     *                  the call.
     * @param arguments the arguments passed to the method.
     * @return the list of results, or <tt>null</tt> if there is no result.
     * @throws NoSuchMethodException if there is no method with that name.
     */
    Object[] invoke(String method, Context context, Object... arguments) throws Exception;
}

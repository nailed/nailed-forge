package jk_5.nailed.api.scripting;

/**
 * Return objects implementing this interface to expose objects with methods to lua.
 *
 * @author jk-5
 */
public interface ILuaObject{

    /**
     * Should return an array of strings that identify the methods that this
     * object exposes to Lua. This will be called once before each attachment,
     * and should not change when called multiple times.
     * @return 	An array of strings representing method names.
     * @see 	#callMethod
     */
    public String[] getMethodNames();

    /**
     * This is called when a lua program on a machine calls one of the methods exposed by getMethodNames().<br>
     * <br>
     * Be aware that this will be called from the Lua thread, and must be thread-safe
     * when interacting with minecraft objects.
     * @param	context		The context of the currently running lua thread. This can be used to wait for events
     *						or otherwise yield.
     * @param	method		An integer identifying which of the methods from getMethodNames() the machine
     *						wishes to call. The integer indicates the index into the getMethodNames() table
     *						that corresponds to the string passed into the api call
     * @param	arguments	An array of objects, representing the arguments passed into the method call.<br>
     *						Lua values of type "string" will be represented by Object type String.<br>
     *						Lua values of type "number" will be represented by Object type Double.<br>
     *						Lua values of type "boolean" will be represented by Object type Boolean.<br>
     *						Lua values of any other type will be represented by a null object.<br>
     *						This array will be empty if no arguments are passed.
     * @return 	An array of objects, representing values you wish to return to the lua program.<br>
     *			Integers, Doubles, Floats, Strings, Booleans and null be converted to their corresponding lua type.<br>
     *			All other types will be converted to nil.<br>
     *			You may return null to indicate no values should be returned.
     * @throws	Exception	If you throw any exception from this function, a lua error will be raised with the
     *						same message as your exception. Use this to throw appropriate errors if the wrong
     *						arguments are supplied to your method.
     * @see 	#getMethodNames
     */
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception;
}

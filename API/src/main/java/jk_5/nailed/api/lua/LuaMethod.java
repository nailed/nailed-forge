package jk_5.nailed.api.lua;

import java.lang.annotation.*;

/**
 * No description given
 *
 * @author jk-5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LuaMethod {

    /**
     * The name under which to make the callback available.
     * <p/>
     * This defaults to the name of the annotated method if left empty.
     */
    String value() default "";

    /**
     * Whether this function may be called directly from the computer's executor
     * thread instead of from the server thread.
     * <p/>
     * You will have to ensure anything your callback does is thread safe when
     * setting this to <tt>true</tt>. Use this for minor lookups, for example.
     * This is mainly intended to allow functions to perform faster than when
     * called 'synchronously' (where the call takes at least one server tick).
     */
    boolean direct() default false;

    /**
     * The maximum number of direct calls that may be performed on this
     * component in a single <em>tick</em>.
     * <p/>
     * You should generally apply a limit if the callback allocates persisting
     * memory (i.e. memory that isn't freed once the call returns), sends
     * network messages, or uses any other kind of resource for which it'd be
     * bad if it were to be used from user programs in an unchecked, unregulated
     * manner.
     */
    int limit() default Integer.MAX_VALUE;

    /**
     * A documentation string that is made available to the computers the
     * component this callback belongs to is connected to. This allows for
     * ingame documentation of callbacks.
     * <p/>
     * You may want to give a short description of what a method does here, but
     * more importantly you should document the expected parameters and return
     * type here.
     * <p/>
     */
    String doc() default "";
}

package jk_5.nailed.api.lua;

import java.lang.annotation.*;

/**
 * No description given
 *
 * @author jk-5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LuaApi {

    String[] value();
}

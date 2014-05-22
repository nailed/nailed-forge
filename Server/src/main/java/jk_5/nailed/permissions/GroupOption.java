package jk_5.nailed.permissions;

import java.lang.annotation.*;

/**
 * No description given
 *
 * @author jk-5
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GroupOption {

    String value();
}

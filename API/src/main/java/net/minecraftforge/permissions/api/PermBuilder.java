package net.minecraftforge.permissions.api;

import net.minecraftforge.permissions.api.context.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface PermBuilder<T extends PermBuilder> {

    boolean check();
    T setUserName(String name);
    T setPermNode(String node);
    T setTargetContext(IContext context);
    T setUserContext(IContext context);
}

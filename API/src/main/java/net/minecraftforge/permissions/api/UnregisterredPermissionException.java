package net.minecraftforge.permissions.api;

/**
 * No description given
 *
 * @author jk-5
 */
public class UnregisterredPermissionException extends RuntimeException {

    public final String node;

    public UnregisterredPermissionException(String node) {
        super("Unregisterred Permission encountered! " + node);
        this.node = node;
    }
}

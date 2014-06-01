package jk_5.nailed.server.command;

/**
 * No description given
 *
 * @author jk-5
 */
public interface SubpermissionCommand {

    void registerPermissions(String owner);

    boolean hasPermission(String sender, String[] args);
}

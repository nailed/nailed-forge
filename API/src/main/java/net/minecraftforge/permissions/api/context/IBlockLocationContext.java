package net.minecraftforge.permissions.api.context;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IBlockLocationContext extends ILocationContext {

    int getBlockX();
    int getBlockY();
    int getBlockZ();
}

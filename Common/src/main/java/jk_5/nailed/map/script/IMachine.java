package jk_5.nailed.map.script;

import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IMachine extends ITerminal {

    public int getId();
    public boolean isOn();
    public void shutdown();
    public void reboot();
    public void queueEvent(String event, Object... args);
    public World getWorld();
}

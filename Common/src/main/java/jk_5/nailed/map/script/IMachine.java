package jk_5.nailed.map.script;

import net.minecraft.world.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IMachine extends ITerminal {

    int getId();
    boolean isOn();
    void shutdown();
    void reboot();
    void queueEvent(String event, Object... args);
    World getWorld();
}

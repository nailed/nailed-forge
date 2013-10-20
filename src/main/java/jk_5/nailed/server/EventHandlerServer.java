package jk_5.nailed.server;

import cpw.mods.fml.common.network.PacketDispatcher;
import jk_5.nailed.network.packets.PacketTimeTracker;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class EventHandlerServer {

    @ForgeSubscribe
    public void onWorldLoad(WorldEvent.Load event){

    }
}

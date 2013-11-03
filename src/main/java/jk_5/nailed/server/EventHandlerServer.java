package jk_5.nailed.server;

import cpw.mods.fml.common.network.PacketDispatcher;
import jk_5.nailed.network.packets.PacketNotification;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class EventHandlerServer {

    @ForgeSubscribe
    public void onChat(ServerChatEvent event){
        PacketDispatcher.sendPacketToAllPlayers(new PacketNotification(event.message).getPacket());
    }
}

package jk_5.nailed.server;

import jk_5.nailed.network.NailedSPH;
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
        NailedSPH.broadcastNotification(event.message);
    }
}

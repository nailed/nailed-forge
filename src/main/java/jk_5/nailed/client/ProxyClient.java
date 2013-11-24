package jk_5.nailed.client;

import codechicken.lib.packet.PacketCustom;
import jk_5.nailed.NailedLog;
import jk_5.nailed.NailedModContainer;
import jk_5.nailed.client.render.NotificationRenderer;
import jk_5.nailed.client.render.RenderEventHandler;
import jk_5.nailed.network.NailedCPH;
import jk_5.nailed.server.ProxyCommon;
import jk_5.nailed.util.updateNotifier.UpdateNotificationManager;
import net.minecraftforge.common.MinecraftForge;

/**
 * No description given
 *
 * @author jk-5
 */
public class ProxyClient extends ProxyCommon {

    public ProxyClient(){
        super();
    }

    @Override
    public void initNetworkHandlers() {
        super.initNetworkHandlers();

        PacketCustom.assignHandler(NailedModContainer.getInstance(), new NailedCPH());
    }

    @Override
    public void registerEventHandlers(){
        super.registerEventHandlers();

        NailedLog.info("Registering Client EventHandlers");
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new SoundManager());
        MinecraftForge.EVENT_BUS.register(new NotificationRenderer());

        NailedLog.info("Initializing UpdateNotifier");
        UpdateNotificationManager.init();
    }
}

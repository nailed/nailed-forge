package jk_5.nailed.client;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import jk_5.nailed.NailedLog;
import jk_5.nailed.client.render.NotificationRenderer;
import jk_5.nailed.client.render.RenderEventHandler;
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
    public void initNetworkHandlers(){
        super.initNetworkHandlers();

        NetworkRegistry.instance().registerChannel(new PacketHandlerClient(), "Nailed", Side.CLIENT);
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

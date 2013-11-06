package jk_5.nailed.server;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import jk_5.nailed.map.teleport.TeleportListener;
import jk_5.nailed.map.teleport.TeleportListenerEffects;
import jk_5.nailed.network.NailedConnectionHandler;
import jk_5.nailed.network.NailedPlayerTracker;
import jk_5.nailed.players.PlayerRegistry;
import net.minecraftforge.common.MinecraftForge;

/**
 * No description given
 *
 * @author jk-5
 */
public class ProxyCommon {

    public static int providerID;

    public ProxyCommon(){

    }

    public void initNetworkHandlers(){
        NetworkRegistry.instance().registerChannel(new PacketHandlerServer(), "Nailed", Side.SERVER);
        NetworkRegistry.instance().registerConnectionHandler(new NailedConnectionHandler());
        GameRegistry.registerPlayerTracker(new NailedPlayerTracker());
    }

    public void registerEventHandlers(){
        MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
        MinecraftForge.EVENT_BUS.register(new TeleportListener());
        MinecraftForge.EVENT_BUS.register(new TeleportListenerEffects());
        MinecraftForge.EVENT_BUS.register(PlayerRegistry.instance());
    }
}

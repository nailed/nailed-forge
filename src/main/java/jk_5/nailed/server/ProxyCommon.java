package jk_5.nailed.server;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import jk_5.nailed.NailedModContainer;
import jk_5.nailed.achievement.AchievementEventListener;
import jk_5.nailed.map.teleport.TeleportListener;
import jk_5.nailed.map.teleport.TeleportListenerEffects;
import jk_5.nailed.network.NailedConnectionHandler;
import jk_5.nailed.network.NailedPlayerTracker;
import jk_5.nailed.network.NailedSPH;
import jk_5.nailed.players.PlayerRegistry;
import lombok.NoArgsConstructor;
import net.minecraftforge.common.MinecraftForge;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
public class ProxyCommon {

    public static int providerID;

    public void initNetworkHandlers(){
        NetworkRegistry.instance().registerConnectionHandler(new NailedConnectionHandler());
        GameRegistry.registerPlayerTracker(new NailedPlayerTracker());
        PacketCustom.assignHandler(NailedModContainer.getInstance(), new NailedSPH());
    }

    public void registerEventHandlers(){
        MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
        MinecraftForge.EVENT_BUS.register(new TeleportListener());
        MinecraftForge.EVENT_BUS.register(new TeleportListenerEffects());
        MinecraftForge.EVENT_BUS.register(PlayerRegistry.instance());
        MinecraftForge.EVENT_BUS.register(new AchievementEventListener());
    }
}

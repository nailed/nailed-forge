package jk_5.nailed.server;

import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import jk_5.nailed.NailedModContainer;
import jk_5.nailed.achievement.AchievementEventListener;
import jk_5.nailed.map.teleport.TeleportListener;
import jk_5.nailed.map.teleport.TeleportListenerEffects;
import jk_5.nailed.network.NailedConnectionHandler;
import jk_5.nailed.network.NailedSPH;
import jk_5.nailed.players.PlayerRegistry;
import jk_5.nailed.util.invsee.InvSeeTicker;
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
        PacketCustom.assignHandler(NailedModContainer.getInstance(), new NailedSPH());
        TickRegistry.registerTickHandler(new InvSeeTicker(), Side.SERVER);
    }

    public void registerEventHandlers(){
        MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
        MinecraftForge.EVENT_BUS.register(new TeleportListener());
        MinecraftForge.EVENT_BUS.register(new TeleportListenerEffects());
        MinecraftForge.EVENT_BUS.register(PlayerRegistry.instance());
        MinecraftForge.EVENT_BUS.register(new AchievementEventListener());
    }
}

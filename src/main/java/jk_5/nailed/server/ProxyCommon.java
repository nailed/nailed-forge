package jk_5.nailed.server;

import cpw.mods.fml.common.FMLCommonHandler;
import jk_5.nailed.achievement.AchievementEventListener;
import jk_5.nailed.network.NailedConnectionHandler;
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

    public void registerEventHandlers(){
        MinecraftForge.EVENT_BUS.register(PlayerRegistry.instance());
        MinecraftForge.EVENT_BUS.register(new AchievementEventListener());
        FMLCommonHandler.instance().bus().register(new InvSeeTicker());
        FMLCommonHandler.instance().bus().register(new NailedConnectionHandler());
    }
}

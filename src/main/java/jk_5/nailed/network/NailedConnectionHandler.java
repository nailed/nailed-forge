package jk_5.nailed.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.embedded.EmbeddedChannel;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.gen.NailedWorldProvider;
import jk_5.nailed.server.ProxyCommon;
import net.minecraftforge.common.network.ForgeMessage;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedConnectionHandler {

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        if(event.player.worldObj.provider instanceof NailedWorldProvider){
            //TODO: send data?
        }
        for(Map map : MapLoader.instance().getMaps()){
            if(map.getID() >= -1 && map.getID() <= 1){
                continue;
            }
            ForgeMessage.DimensionRegisterMessage packet = new ForgeMessage.DimensionRegisterMessage(map.getID(), ProxyCommon.providerID);
            EmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(event.player);
            channel.writeOutbound(packet);
        }
    }
}

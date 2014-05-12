package jk_5.nailed.network.handlers;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkHandshakeEstablished;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jk_5.nailed.NailedServer;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import net.minecraftforge.common.network.ForgeMessage;

/**
 * No description given
 *
 * @author jk-5
 */
public class FMLHandshakeHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
        if(evt instanceof NetworkHandshakeEstablished){
            NetworkHandshakeEstablished event = (NetworkHandshakeEstablished) evt;
            FMLEmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DISPATCHER);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(event.dispatcher);
            for(Map map : NailedAPI.getMapLoader().getMaps()){
                if(map.getID() >= -1 && map.getID() <= 1){
                    continue;
                }
                channel.writeOutbound(new ForgeMessage.DimensionRegisterMessage(map.getID(), NailedServer.getProviderID()));
            }
        }
        ctx.fireUserEventTriggered(evt);
    }
}

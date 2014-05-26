package jk_5.nailed.network.handlers;

import io.netty.channel.*;

import cpw.mods.fml.common.network.*;
import cpw.mods.fml.relauncher.*;

import net.minecraftforge.common.network.*;

import jk_5.nailed.*;
import jk_5.nailed.api.*;
import jk_5.nailed.api.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class FMLHandshakeHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
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

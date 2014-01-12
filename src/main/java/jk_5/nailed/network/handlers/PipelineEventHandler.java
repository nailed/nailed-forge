package jk_5.nailed.network.handlers;

import cpw.mods.fml.common.network.FMLNetworkEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jk_5.nailed.NailedModContainer;

/**
 * No description given
 *
 * @author jk-5
 */
public class PipelineEventHandler extends ChannelInboundHandlerAdapter {

    private boolean connected = false;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
        if(evt instanceof FMLNetworkEvent.ClientConnectedToServerEvent){
            this.connected = true;
        }else if(evt instanceof FMLNetworkEvent.ClientDisconnectionFromServerEvent){
            if(this.connected){
                NailedModContainer.getInstance().unregisterDimensions();
                this.connected = false;
            }
        }

        ctx.fireUserEventTriggered(evt);
    }
}

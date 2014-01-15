package jk_5.nailed.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * No description given
 *
 * @author jk-5
 */
public class PipelineEventHandler extends ChannelInboundHandlerAdapter {

    private boolean connected = false;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
        /*if(evt instanceof FMLNetworkEvent.ClientConnectedToServerEvent){
            this.connected = true;
        }else if(evt instanceof FMLNetworkEvent.ClientDisconnectionFromServerEvent){
            if(this.connected){
                NailedServer.getInstance().unregisterDimensions();
                this.connected = false;
            }
        }*/
        //TODO: move to client!
        ctx.fireUserEventTriggered(evt);
    }
}

package jk_5.nailed.client.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.render.RenderEventHandler;
import jk_5.nailed.network.NailedPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class TimeUpdateHandler extends SimpleChannelInboundHandler<NailedPacket.TimeUpdate> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.TimeUpdate msg) throws Exception{
        RenderEventHandler.doRender = msg.display;
        RenderEventHandler.format = msg.data;
    }
}

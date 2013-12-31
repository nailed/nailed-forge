package jk_5.nailed.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.network.NailedPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class MovementEventHandler extends SimpleChannelInboundHandler<NailedPacket.MovementEvent> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.MovementEvent msg) throws Exception{

    }
}

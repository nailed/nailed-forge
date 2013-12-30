package jk_5.nailed.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * No description given
 *
 * @author jk-5
 */
@ChannelHandler.Sharable
public class NailedPacketHandler extends SimpleChannelInboundHandler<NailedPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket msg) throws Exception{

    }
}

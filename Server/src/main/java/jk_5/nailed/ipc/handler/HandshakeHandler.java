package jk_5.nailed.ipc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jk_5.nailed.ipc.packet.PacketInitConnection;

/**
 * No description given
 *
 * @author jk-5
 */
public class HandshakeHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        ctx.fireChannelActive();

        ctx.writeAndFlush(new PacketInitConnection());
    }
}

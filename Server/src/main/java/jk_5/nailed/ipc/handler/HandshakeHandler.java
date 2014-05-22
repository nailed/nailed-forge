package jk_5.nailed.ipc.handler;

import io.netty.channel.*;

import jk_5.nailed.ipc.packet.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class HandshakeHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();

        ctx.writeAndFlush(new PacketIdentify());
        ctx.writeAndFlush(new PacketInitConnection());
    }
}

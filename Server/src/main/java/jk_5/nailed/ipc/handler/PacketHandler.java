package jk_5.nailed.ipc.handler;

import io.netty.channel.*;

import jk_5.nailed.ipc.packet.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketHandler extends SimpleChannelInboundHandler<IpcPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IpcPacket msg) throws Exception {
        msg.processPacket();
    }
}

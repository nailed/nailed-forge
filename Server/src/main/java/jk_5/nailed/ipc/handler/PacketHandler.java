package jk_5.nailed.ipc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.ipc.packet.IpcPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketHandler extends SimpleChannelInboundHandler<IpcPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IpcPacket msg) throws Exception{
        msg.processPacket();
    }
}

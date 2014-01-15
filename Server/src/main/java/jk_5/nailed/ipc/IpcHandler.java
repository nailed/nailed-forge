package jk_5.nailed.ipc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.ipc.packet.IpcPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class IpcHandler extends SimpleChannelInboundHandler<IpcPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IpcPacket msg) throws Exception {
        if(msg.canBeHandledASync()){
            msg.processPacket();
        }else{
            PacketManager.getProcessQueue().add(msg);
        }
    }
}

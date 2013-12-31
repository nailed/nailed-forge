package jk_5.nailed.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.network.NailedPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class GuiReturnDataHandler extends SimpleChannelInboundHandler<NailedPacket.GuiReturnDataPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.GuiReturnDataPacket msg) throws Exception{

    }
}

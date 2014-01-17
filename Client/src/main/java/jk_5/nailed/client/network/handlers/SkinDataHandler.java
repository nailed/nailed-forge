package jk_5.nailed.client.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.network.NailedPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class SkinDataHandler extends SimpleChannelInboundHandler<NailedPacket.PlayerSkin> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.PlayerSkin msg) throws Exception{

    }
}

package jk_5.nailed.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;

/**
 * No description given
 *
 * @author jk-5
 */
public class FPSSummaryHandler extends SimpleChannelInboundHandler<NailedPacket.FPSSummary> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.FPSSummary msg) throws Exception{
        Player p = PlayerRegistry.instance().getPlayer(NailedNetworkHandler.getPlayer(ctx));
        p.setFps(msg.fps);
    }
}

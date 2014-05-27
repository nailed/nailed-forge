package jk_5.nailed.network.handlers;

import io.netty.channel.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.network.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class FPSSummaryHandler extends SimpleChannelInboundHandler<NailedPacket.FPSSummary> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.FPSSummary msg) throws Exception {
        Player p = NailedAPI.getPlayerRegistry().getPlayer(NailedNetworkHandler.getPlayer(ctx));
        p.setFps(msg.fps);
    }
}

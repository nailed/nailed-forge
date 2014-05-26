package jk_5.nailed.network.handlers;

import io.netty.channel.*;

import jk_5.nailed.ipc.*;
import jk_5.nailed.network.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class LoginHandler extends SimpleChannelInboundHandler<NailedPacket.Login> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, NailedPacket.Login msg) throws Exception {
        IpcEventListener.loginPlayer(NailedNetworkHandler.getPlayer(ctx), msg.username, msg.password);
    }
}

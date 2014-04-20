package jk_5.nailed.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.ipc.IpcManager;
import jk_5.nailed.ipc.packet.PacketCreateAccount;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class RegisterHandler extends SimpleChannelInboundHandler<NailedPacket.CreateAccount> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.CreateAccount msg) throws Exception{
        IpcManager.instance().sendPacket(new PacketCreateAccount(NailedAPI.getPlayerRegistry().getPlayer(NailedNetworkHandler.getPlayer(ctx)).getId(), msg.username, msg.email, msg.name, msg.password));
    }
}

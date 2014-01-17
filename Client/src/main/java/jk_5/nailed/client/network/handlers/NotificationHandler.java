package jk_5.nailed.client.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.network.NailedPacket;
import jk_5.nailed.client.render.NotificationRenderer;

/**
 * No description given
 *
 * @author jk-5
 */
public class NotificationHandler extends SimpleChannelInboundHandler<NailedPacket.Notification> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.Notification msg) throws Exception{
        NotificationRenderer.addNotification(msg.message, msg.icon, msg.color);
    }
}

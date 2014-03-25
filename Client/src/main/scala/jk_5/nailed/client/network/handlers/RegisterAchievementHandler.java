package jk_5.nailed.client.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.achievement.NailedAchievements;
import jk_5.nailed.network.NailedPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class RegisterAchievementHandler extends SimpleChannelInboundHandler<NailedPacket.RegisterAchievement> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.RegisterAchievement msg) throws Exception{
        NailedAchievements.register(msg.enable);
    }
}

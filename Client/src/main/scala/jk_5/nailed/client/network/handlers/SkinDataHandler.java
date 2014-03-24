package jk_5.nailed.client.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.skinsync.SkinSync;
import jk_5.nailed.network.NailedPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class SkinDataHandler extends SimpleChannelInboundHandler<NailedPacket.PlayerSkin> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.PlayerSkin msg) throws Exception{
        if(msg.isSkin){
            SkinSync.getInstance().setPlayerSkinName(msg.username, msg.skin);
        }else{
            SkinSync.getInstance().setPlayerCloakName(msg.username, msg.skin);
        }
    }
}

package jk_5.nailed.client.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.map.edit.MapEditManager;
import jk_5.nailed.network.NailedPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class MapEditHandler extends SimpleChannelInboundHandler<NailedPacket.EditMode> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.EditMode msg) throws Exception{
        MapEditManager.instance().setEnabled(msg.enable);
        if(msg.enable){
            MapEditManager.instance().readData(msg.buffer);
        }
    }
}

package jk_5.nailed.client.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.map.NailedWorldProvider;
import jk_5.nailed.network.NailedPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class MapDataHandler extends SimpleChannelInboundHandler<NailedPacket.MapData> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.MapData msg) throws Exception{
        World world = Minecraft.getMinecraft().theWorld;
        if(world.provider.dimensionId == msg.dimId && world.provider instanceof NailedWorldProvider){
            ((NailedWorldProvider) world.provider).readData(msg.data);
        }
    }
}

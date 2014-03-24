package jk_5.nailed.client.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.blocks.tileentity.NailedTileEntity;
import jk_5.nailed.network.NailedPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

/**
 * No description given
 *
 * @author jk-5
 */
public class TileEntityDataHandler extends SimpleChannelInboundHandler<NailedPacket.TileEntityData> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.TileEntityData msg) throws Exception{
        TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(msg.x, msg.y, msg.z);
        if(tile != null && tile instanceof NailedTileEntity){
            ((NailedTileEntity) tile).readData(msg.data);
        }
    }
}

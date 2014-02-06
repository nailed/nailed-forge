package jk_5.nailed.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import jk_5.nailed.util.IMovementEventTileEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

/**
 * No description given
 *
 * @author jk-5
 */
public class MovementEventHandler extends SimpleChannelInboundHandler<NailedPacket.MovementEvent> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.MovementEvent msg) throws Exception{
        EntityPlayerMP player = NailedNetworkHandler.getPlayer(ctx);
        TileEntity tile = player.worldObj.getTileEntity(msg.x, msg.y, msg.z);
        if(tile != null && tile instanceof IMovementEventTileEntity){
            ((IMovementEventTileEntity) tile).onMovementEvent(msg.type, player);
        }
    }
}

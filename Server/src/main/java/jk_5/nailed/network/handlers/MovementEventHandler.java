package jk_5.nailed.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.blocks.NailedBlocks;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import jk_5.nailed.util.ElevatorHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class MovementEventHandler extends SimpleChannelInboundHandler<NailedPacket.MovementEvent> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.MovementEvent msg) throws Exception{
        EntityPlayerMP player = NailedNetworkHandler.getPlayer(ctx);
        World world = player.worldObj;
        if(world.getBlock(msg.x, msg.y, msg.z) == NailedBlocks.stat && world.getBlockMetadata(msg.x, msg.y, msg.z) == 2){
            if(msg.type == 0){
                ElevatorHelper.onJump(player);
            }else if(msg.type == 1){
                ElevatorHelper.onSneak(player);
            }
        }
    }
}

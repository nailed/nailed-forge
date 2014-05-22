package jk_5.nailed.network.handlers;

import io.netty.channel.*;

import net.minecraft.entity.player.*;
import net.minecraft.world.*;

import jk_5.nailed.blocks.*;
import jk_5.nailed.network.*;
import jk_5.nailed.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class MovementEventHandler extends SimpleChannelInboundHandler<NailedPacket.MovementEvent> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.MovementEvent msg) throws Exception {
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

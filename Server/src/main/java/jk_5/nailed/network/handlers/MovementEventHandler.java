package jk_5.nailed.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.blocks.NailedBlocks;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class MovementEventHandler extends SimpleChannelInboundHandler<NailedPacket.MovementEvent> {

    private static final int MAX_DISTANCE = 32;
    private static final int MAX_PASSABLE_BLOCKS = 32;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.MovementEvent msg) throws Exception{
        EntityPlayerMP player = NailedNetworkHandler.getPlayer(ctx);
        World world = player.worldObj;
        if(world.getBlock(msg.x, msg.y, msg.z) == NailedBlocks.stat && world.getBlockMetadata(msg.x, msg.y, msg.z) == 2)
        if(msg.type == 0){
            this.activate(msg.x, msg.y, msg.z, player, 1);
        }else if(msg.type == 1){
            this.activate(msg.x, msg.y, msg.z, player, -1);
        }
    }

    private boolean canTeleportPlayer(World world, int x, int y, int z){
        Block block = world.getBlock(x, y, z);
        if(world.isAirBlock(x, y, z)) return true;

        final AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(world, x, y, z);
        return aabb == null || aabb.getAverageEdgeLength() < 0.7;
    }

    private int findLevel(int xCoord, int yCoord, int zCoord, World world, int delta){
        int blocksInTheWay = 0;
        for(int i = 0, y = yCoord; i < MAX_DISTANCE; i++){
            y += delta;
            if(!world.blockExists(xCoord, y, zCoord)) break;

            Block block = world.getBlock(xCoord, y, zCoord);
            int meta = world.getBlockMetadata(xCoord, y, zCoord);

            if(world.isAirBlock(xCoord, y, zCoord)) continue;

            if(block == NailedBlocks.stat && meta == 2){
                if(canTeleportPlayer(world, xCoord, y + 1, zCoord) && canTeleportPlayer(world, xCoord, y + 2, zCoord)){
                    return y;
                }
            }

            if(!block.isNormalCube(world, xCoord, y, zCoord) && ++blocksInTheWay > MAX_PASSABLE_BLOCKS){
                break;
            }
        }

        return -1;
    }

    private void activate(int x, int y, int z, EntityPlayer player, int delta){
        int level = findLevel(x, y, z, player.worldObj, delta);
        if(level >= 0){
            player.setPositionAndUpdate(x + 0.5, level + 1.1, z + 0.5);
            player.worldObj.playSoundAtEntity(player, "nailed:elevator", 1F, 1F);
        }
    }
}

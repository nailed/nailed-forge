package jk_5.nailed.util;

import com.google.common.base.Preconditions;
import jk_5.nailed.blocks.NailedBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * No description given
 *
 * @author jk-5
 */
public class ElevatorHelper {

    private static final int MAX_DISTANCE = 32;
    private static final int MAX_PASSABLE_BLOCKS = 32;

    public static void onJump(@Nonnull EntityPlayerMP player){
        Preconditions.checkNotNull(player, "player");

        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.boundingBox.minY) - 1;
        int z = MathHelper.floor_double(player.posZ);
        Block block = player.worldObj.getBlock(x, y, z);
        int meta = player.worldObj.getBlockMetadata(x, y, z);
        if (block == NailedBlocks.stat && meta == 2) {
            activate(x, y, z, player, 1);
        }
    }

    public static void onSneak(@Nonnull EntityPlayerMP player) {
        Preconditions.checkNotNull(player, "player");

        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.boundingBox.minY) - 1;
        int z = MathHelper.floor_double(player.posZ);
        Block block = player.worldObj.getBlock(x, y, z);
        int meta = player.worldObj.getBlockMetadata(x, y, z);
        if (block == NailedBlocks.stat && meta == 2) {
            activate(x, y, z, player, -1);
        }
    }

    private static boolean canTeleportPlayer(World world, int x, int y, int z){
        Block block = world.getBlock(x, y, z);
        if(world.isAirBlock(x, y, z)) return true;

        final AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(world, x, y, z);
        return aabb == null || aabb.getAverageEdgeLength() < 0.7;
    }

    private static int findLevel(int xCoord, int yCoord, int zCoord, World world, int delta){
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

    private static void activate(int x, int y, int z, EntityPlayer player, int delta){
        int level = findLevel(x, y, z, player.worldObj, delta);
        if(level >= 0){
            player.setPositionAndUpdate(x + 0.5, level + 1.1, z + 0.5);
            player.worldObj.playSoundAtEntity(player, "nailed:elevator", 1F, 1F);
        }
    }
}

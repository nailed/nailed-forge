package jk_5.nailed.blocks.tileentity;

import com.google.common.base.Preconditions;
import jk_5.nailed.blocks.NailedBlocks;
import jk_5.nailed.util.IMovementEventTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * No description given
 *
 * @author jk-5
 */
public class TileEntityElevator extends NailedTileEntity implements IMovementEventTileEntity {

    private static final int MAX_DISTANCE = 32;
    private static final int MAX_PASSABLE_BLOCKS = 32;

    private boolean canTeleportPlayer(int x, int y, int z){
        Block block = this.worldObj.getBlock(x, y, z);
        if(this.worldObj.isAirBlock(x, y, z)) return true;

        final AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(this.worldObj, x, y, z);
        return aabb == null || aabb.getAverageEdgeLength() < 0.7;
    }

    private int findLevel(ForgeDirection direction){
        Preconditions.checkArgument(direction == ForgeDirection.UP || direction == ForgeDirection.DOWN, "Must be either up or down");

        int blocksInTheWay = 0;
        final int delta = direction.offsetY;
        for(int i = 0, y = this.yCoord; i < MAX_DISTANCE; i++){
            y += delta;
            if(!this.worldObj.blockExists(this.xCoord, y, this.zCoord)) break;

            Block block = this.worldObj.getBlock(this.xCoord, y, this.zCoord);
            int meta = this.worldObj.getBlockMetadata(this.xCoord, y, this.zCoord);

            if(block instanceof BlockAir) continue;

            if(block == NailedBlocks.stat && meta == 2){
                TileEntity otherBlock = this.worldObj.getTileEntity(this.xCoord, y, this.zCoord);
                if(otherBlock instanceof TileEntityElevator){
                    if(canTeleportPlayer(this.xCoord, y + 1, this.zCoord) && canTeleportPlayer(this.xCoord, y + 2, this.zCoord)){
                        return y;
                    }
                }
            }

            if(!block.isNormalCube(this.worldObj, this.xCoord, y, this.zCoord) && (++blocksInTheWay > MAX_PASSABLE_BLOCKS)){
                break;
            }
        }

        return -1;
    }

    private void activate(EntityPlayer player, ForgeDirection dir){
        int level = findLevel(dir);
        if(level >= 0){
            player.setPositionAndUpdate(this.xCoord + 0.5, level + 1.1, this.zCoord + 0.5);
            this.worldObj.playSoundAtEntity(player, "nailed:elevator", 1F, 1F);
        }
    }

    @Override
    public void onMovementEvent(int type, EntityPlayerMP player){
        if(type == 0){
            this.activate(player, ForgeDirection.UP);
        }else if(type == 1){
            this.activate(player, ForgeDirection.DOWN);
        }
    }

    @Override
    public boolean canUpdate(){
        return false;
    }
}

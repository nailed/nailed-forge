package jk_5.nailed.blocks.tileentity;

import com.google.common.base.Preconditions;
import jk_5.nailed.blocks.NailedBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * No description given
 *
 * @author jk-5
 */
public class TileEntityElevator extends NailedTileEntity {

    private static final int MAX_DISTANCE = 32;
    private static final int MAX_PASSABLE_BLOCKS = 32;

    private boolean canTeleportPlayer(int x, int y, int z) {
        Block block = this.field_145850_b.func_147439_a(x, y, z);
        if (this.field_145850_b.func_147437_c(x, y, z)) return true;

        final AxisAlignedBB aabb = block.func_149668_a(field_145850_b, x, y, z);
        return aabb == null || aabb.getAverageEdgeLength() < 0.7;
    }

    private int findLevel(ForgeDirection direction) {
        Preconditions.checkArgument(direction == ForgeDirection.UP || direction == ForgeDirection.DOWN, "Must be either up or down");

        int blocksInTheWay = 0;
        final int delta = direction.offsetY;
        for (int i = 0, y = this.field_145848_d; i < MAX_DISTANCE; i++) {
            y += delta;
            if (!this.field_145850_b.blockExists(this.field_145851_c, y, this.field_145849_e)) break;

            Block block = this.field_145850_b.func_147439_a(this.field_145851_c, y, this.field_145849_e);
            int meta = this.field_145850_b.getBlockMetadata(this.field_145851_c, y, this.field_145849_e);

            if (block instanceof BlockAir) continue;

            if (block == NailedBlocks.stat && meta == 2) {
                TileEntity otherBlock = this.field_145850_b.func_147438_o(this.field_145851_c, y, this.field_145849_e);
                if (otherBlock instanceof TileEntityElevator) {
                    if (canTeleportPlayer(this.field_145851_c, y + 1, this.field_145849_e) && canTeleportPlayer(this.field_145851_c, y + 2, this.field_145849_e)) return y;
                }
            }

            if (!block.isNormalCube(this.field_145850_b, this.field_145851_c, y, this.field_145849_e) && (++blocksInTheWay > MAX_PASSABLE_BLOCKS)) break;
        }

        return -1;
    }

    private void activate(EntityPlayer player, ForgeDirection dir) {
        int level = findLevel(dir);
        if (level >= 0) {             //this.xCoord                             this.zCoord
            player.setPositionAndUpdate(this.field_145851_c + 0.5, level + 1.1, this.field_145849_e + 0.5);
            //this.worldObj
            this.field_145850_b.playSoundAtEntity(player, "nailed:teleport", 1F, 1F);
        }
    }

    public void onMovementEvent(byte b, EntityPlayer player){
        switch(b){
            case 0:
                activate(player, ForgeDirection.UP);
                break;
            case 1:
                activate(player, ForgeDirection.DOWN);
                break;
        }
    }
}

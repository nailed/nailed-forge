package jk_5.nailed.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.init.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;

import jk_5.nailed.blocks.tileentity.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockPortalCrystal extends NailedBlock {

    public BlockPortalCrystal() {
        super("portalCrystal", Material.glass);
        this.setBlockUnbreakable();
        this.setBlockTextureName("nailed:crystal");
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if(world.isRemote){
            return;
        }
        if(world.getBlockMetadata(x, y, z) == 0){
            return;
        }
        TileEntity tile = BlockPortalController.getTileEntity(world, x, y, z);
        if(tile == null || !(tile instanceof TileEntityPortalController) || ((TileEntityPortalController) tile).getDestination() == null){
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
            BlockPortalController.unpath(world, x, y, z);
        }
    }

    @Override
    public Block getReplacementBlock() {
        return Blocks.glowstone;
    }

    @Override
    public int getReplacementMetadata() {
        return 0;
    }
}

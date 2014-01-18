package jk_5.nailed.blocks;

import jk_5.nailed.blocks.tileentity.TileEntityPortalController;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockPortalCrystal extends NailedBlock {

    public BlockPortalCrystal(){
        super("portalCrystal", Material.field_151592_s); //Material.glass
        this.func_149722_s();
        this.func_149658_d("nailed:crystal");
    }

    @Override
    public void func_149695_a(World world, int x, int y, int z, Block block){
        if(world.isRemote) return;
        if(world.getBlockMetadata(x, y, z) == 0) return;
        TileEntity tile = BlockPortalController.getTileEntity(world, x, y, z);
        if(tile == null || !(tile instanceof TileEntityPortalController) || ((TileEntityPortalController) tile).getDestination() == null){
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
            BlockPortalController.unpath(world, x, y, z);
        }
    }
}
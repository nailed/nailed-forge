package jk_5.nailed.blocks;

import net.minecraft.block.BlockCommandBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.world.World;

import jk_5.nailed.NailedLog;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockCommandBlockOverride extends BlockCommandBlock {

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        NailedLog.info("Oh hey! We have overridden the commandblock");
        return new TileEntityCommandBlock();
    }
}

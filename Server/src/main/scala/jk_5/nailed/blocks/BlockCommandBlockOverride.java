package jk_5.nailed.blocks;

import net.minecraft.block.*;
import net.minecraft.init.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;

import jk_5.nailed.*;
import jk_5.nailed.api.block.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockCommandBlockOverride extends BlockCommandBlock implements INailedBlock {

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        NailedLog.info("Oh hey! We have overridden the commandblock");
        return new TileEntityCommandBlock();
    }

    @Override
    public Block getReplacementBlock() {
        return Blocks.command_block;
    }

    @Override
    public int getReplacementMetadata() {
        return 0;
    }
}

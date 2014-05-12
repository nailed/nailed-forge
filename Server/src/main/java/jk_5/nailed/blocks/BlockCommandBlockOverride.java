package jk_5.nailed.blocks;

import jk_5.nailed.NailedLog;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.world.World;

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

    public static Block getReplacementBlock(){
        return Blocks.command_block;
    }

    public static int getReplacementMetadata(){
        return 0;
    }
}

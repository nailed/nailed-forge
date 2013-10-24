package jk_5.nailed.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockStatEmitter extends NailedBlock implements ITileEntityProvider {

    public BlockStatEmitter(){
        super("statEmitter", Material.circuits);
        this.isBlockContainer = true;
    }

    public TileEntity createNewTileEntity(World world){
        return null;
    }
}

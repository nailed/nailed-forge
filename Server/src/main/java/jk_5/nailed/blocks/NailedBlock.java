package jk_5.nailed.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;

import jk_5.nailed.api.block.*;
import jk_5.nailed.blocks.tileentity.*;

import lombok.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedBlock extends Block implements INailedBlock {

    @Getter
    private final String registeredName;

    public NailedBlock(String name, Material material) {
        super(material);
        this.registeredName = name;
        this.setBlockName("nailed." + name);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entity, int side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile != null && tile instanceof NailedTileEntity){
            return ((NailedTileEntity) tile).onBlockActivated(entity, side, hitX, hitY, hitZ);
        }
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta) {
        super.breakBlock(world, x, y, z, oldBlock, oldMeta);
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventID, int data) {
        super.onBlockEventReceived(world, x, y, z, eventID, data);
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity != null && tileentity.receiveClientEvent(eventID, data);
    }

    public Block getReplacementBlock() {
        return Blocks.air;
    }

    public int getReplacementMetadata() {
        return 0;
    }
}

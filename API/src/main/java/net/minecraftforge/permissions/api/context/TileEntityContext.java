package net.minecraftforge.permissions.api.context;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

/**
 * No description given
 *
 * @author jk-5
 */
public class TileEntityContext implements IBlockContext {

    @Getter private int x, y, z, dimensionId, blockMetadata;
    @Getter private Block block;

    public TileEntityContext(TileEntity tile){
        this.x = tile.xCoord;
        this.y = tile.yCoord;
        this.z = tile.zCoord;
        this.dimensionId = tile.getWorldObj().provider.dimensionId;
        this.blockMetadata = tile.blockMetadata;
        this.block = tile.getBlockType();
    }

    @Override
    public boolean hasTileEntity(){
        return true;
    }

    @Override
    public int getBlockX(){
        return this.x;
    }

    @Override
    public int getBlockY(){
        return this.y;
    }

    @Override
    public int getBlockZ(){
        return this.z;
    }
}

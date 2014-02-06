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

    private int x, y, z;
    @Getter private int dimensionId, blockMetadata;
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
    public double getX(){
        return x;
    }

    @Override
    public double getY(){
        return y;
    }

    @Override
    public double getZ(){
        return z;
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

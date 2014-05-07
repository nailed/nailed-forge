package jk_5.nailed.client.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockLight extends NailedBlock {

    public BlockLight() {
        super("light", Material.glass);

        this.disableStats();
        this.setBlockTextureName("nailed:light");
        this.setBlockUnbreakable();
        this.setLightOpacity(0);
        this.setHardness(6000000.0F);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z){
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public int getRenderType(){
        return -1;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
        return null;
    }

    @Override
    public int getMobilityFlag(){
        return 2;
    }

    @Override
    public boolean renderAsNormalBlock(){
        return false;
    }

    @Override
    public boolean isOpaqueCube(){
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side){
        return false;
    }
}

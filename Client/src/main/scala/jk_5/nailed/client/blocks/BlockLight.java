package jk_5.nailed.client.blocks;

import java.util.*;

import net.minecraft.block.material.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import net.minecraftforge.common.util.*;

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

    @Override
    public void registerBlockIcons(IIconRegister registry) {

    }
}

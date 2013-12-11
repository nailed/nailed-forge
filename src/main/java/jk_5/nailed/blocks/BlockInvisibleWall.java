package jk_5.nailed.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import java.util.Random;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockInvisibleWall extends NailedBlock {

    public BlockInvisibleWall(){
        super("invisibleWall", Material.glass);
        this.disableStats();
        this.setBlockUnbreakable();
        this.setStepSound(new StepSound("null", 0F, 0F));
        this.setLightOpacity(15);
    }

    @Override
    public Icon getIcon(int side, int meta){
        return Block.tallGrass.getIcon(side, meta);
    }

    @Override
    public int quantityDropped(Random random){
        return 0;
    }

    @Override
    public int idDropped(int id, Random random, int par3){
        return 0;
    }

    @Override
    public int getRenderType(){
        return -1;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z){
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public int getMobilityFlag(){
        return 2;
    }

    @Override
    public boolean isOpaqueCube(){
        return false;
    }

    @Override
    public boolean renderAsNormalBlock(){
        return false;
    }

    @Override
    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side){
        return false;
    }
}

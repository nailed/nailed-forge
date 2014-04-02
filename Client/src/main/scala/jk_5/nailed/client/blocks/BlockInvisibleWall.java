package jk_5.nailed.client.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockInvisibleWall extends BlockMulti {

    public BlockInvisibleWall(){
        super("invisibleBlock", Material.glass); //Material.glass

        this.disableStats(); //disableStats
        this.setBlockUnbreakable(); //setBlockUnbreakable
        this.setLightOpacity(0); //setLightOpacity
        this.setHardness(6000000.0F); //setHardness
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z){
        int meta = world.getBlockMetadata(x, y, z);
        return meta == 2 ? 15 : 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack){
        if(stack.getItemDamage() == 0){
            return "invisibleWall";
        }else if(stack.getItemDamage() == 1){
            return "invisibleBlock";
        }else if(stack.getItemDamage() == 2){
            return "invisibleLight";
        }
        return "";
    }

    @Override
    public IIcon getIcon(int side, int meta){
        return Blocks.tallgrass.getIcon(side, meta);
    }

    @Override
    public int quantityDropped(Random random){
        return 0;
    }

    @Override
    public int getRenderType(){
        return -1;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
        if(world.getBlockMetadata(x, y, z) == 1) return null;
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
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
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side){
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item item, CreativeTabs tab, List list){
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
    }
}
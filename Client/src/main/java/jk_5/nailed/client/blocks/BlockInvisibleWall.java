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
        super("invisibleBlock", Material.field_151592_s); //Material.glass

        //this.disableStats(); //disableStats
        this.func_149722_s(); //setBlockUnbreakable
        this.func_149713_g(0); //setLightOpacity
        this.func_149752_b(6000000.0F); //setHardness
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
    public IIcon func_149691_a(int side, int meta){
        return Blocks.tallgrass.func_149691_a(side, meta);
    }

    @Override
    public int func_149745_a(Random random){
        return 0;
    }

    @Override
    public int func_149645_b(){
        return -1;
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int x, int y, int z){
        if(world.getBlockMetadata(x, y, z) == 1) return null;
        return super.func_149668_a(world, x, y, z);
    }

    @Override
    public int func_149656_h(){
        return 2;
    }

    @Override
    public boolean func_149662_c(){
        return false;
    }

    @Override
    public boolean func_149686_d(){
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side){
        return false;
    }

    @Override
    public void func_149666_a(Item item, CreativeTabs tab, List list){
        list.add(new ItemStack(item, 1, 0));
        //list.add(new ItemStack(id, 1, 1));
        list.add(new ItemStack(item, 1, 2));
    }
}

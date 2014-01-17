package jk_5.nailed.client.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.client.blocks.tileentity.TileEntityElevator;
import jk_5.nailed.client.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.client.blocks.tileentity.TileEntityStatModifier;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockStat extends BlockMulti implements ITileEntityProvider {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public BlockStat(){
        super("statBlock", Material.field_151594_q); //Material.circuits
        this.func_149722_s(); //setBlockUnbreakable
    }

    @Override
    public String getUnlocalizedName(ItemStack stack){
        if(stack.getItemDamage() == 0){
            return "statEmitter";
        }else if(stack.getItemDamage() == 1){
            return "statModifier";
        }else if(stack.getItemDamage() == 2){
            return "elevator";
        }
        return "";
    }

    @Override
    public TileEntity func_149915_a(World world, int meta){
        if(meta == 0){
            return new TileEntityStatEmitter();
        }else if(meta == 1){
            return new TileEntityStatModifier();
        }else if(meta == 2){
            return new TileEntityElevator();
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister register){
        this.icons = new IIcon[16];
        this.icons[0] = register.registerIcon("nailed:statemitter");
        this.icons[1] = register.registerIcon("nailed:statmodifier");
        this.icons[2] = register.registerIcon("nailed:elevator");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int side, int meta){
        return this.icons[meta];
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side){
        return world.getBlockMetadata(x, y, z) != 2;
    }

    @Override
    public int func_149692_a(int meta){
        return meta;
    }

    @Override
    public void func_149666_a(Item item, CreativeTabs tab, List list){
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
    }
}

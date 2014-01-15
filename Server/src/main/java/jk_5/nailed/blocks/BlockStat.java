package jk_5.nailed.blocks;

import jk_5.nailed.blocks.tileentity.TileEntityElevator;
import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.blocks.tileentity.TileEntityStatModifier;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class BlockStat extends BlockMulti implements ITileEntityProvider {

    public BlockStat(){
        super("statBlock", Material.field_151594_q); //Material.circuits
        //this.isBlockContainer = true;
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

    @Override
    public int func_149709_b(IBlockAccess world, int x, int y, int z, int side){
        TileEntity tile = world.func_147438_o(x, y, z);
        if(tile == null || !(tile instanceof TileEntityStatEmitter)) return 0;
        return ((TileEntityStatEmitter) tile).isSignalEnabled() ? 15 : 0;
    }
}

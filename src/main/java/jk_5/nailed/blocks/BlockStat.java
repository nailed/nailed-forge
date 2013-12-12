package jk_5.nailed.blocks;

import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.blocks.tileentity.TileEntityStatModifier;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
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
        super("statBlock", Material.circuits);
        this.isBlockContainer = true;
        this.setBlockUnbreakable();
    }

    @Override
    public String getUnlocalizedName(ItemStack stack){
        if(stack.getItemDamage() == 0){
            return "statEmitter";
        }else if(stack.getItemDamage() == 1){
            return "statModifier";
        }
        return "";
    }

    @Override
    public TileEntity createNewTileEntity(World world){
        return null;
    }

    public TileEntity createTileEntity(World world, int meta){
        if(meta == 0){
            return new TileEntityStatEmitter();
        }else if(meta == 1){
            return new TileEntityStatModifier();
        }
        return null;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side){
        return true;
    }

    @Override
    public int damageDropped(int meta){
        return meta;
    }

    @Override
    public void getSubBlocks(int id, CreativeTabs tab, List list){
        list.add(new ItemStack(id, 1, 0));
        list.add(new ItemStack(id, 1, 1));
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side){
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if(tile == null || !(tile instanceof TileEntityStatEmitter)) return 0;
        return ((TileEntityStatEmitter) tile).isSignalEnabled() ? 15 : 0;
    }
}

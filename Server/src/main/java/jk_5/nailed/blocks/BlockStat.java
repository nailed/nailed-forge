package jk_5.nailed.blocks;

import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.blocks.tileentity.TileEntityStatModifier;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
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
        super("statBlock", Material.iron);
        this.setBlockUnbreakable();
    }

    @Override
    public String getUnlocalizedName(ItemStack stack){
        switch (stack.getItemDamage()){
            case 0: return "statEmitter";
            case 1: return "statModifier";
            case 2: return "elevator";
            default: return "";
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta){
        switch (meta){
            case 0: return new TileEntityStatEmitter();
            case 1: return new TileEntityStatModifier();
            default: return null;
        }
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side){
        return world.getBlockMetadata(x, y, z) != 2;
    }

    @Override
    public int damageDropped(int meta){
        return meta;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list){
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side){
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile == null || !(tile instanceof TileEntityStatEmitter)) return 0;
        return ((TileEntityStatEmitter) tile).isSignalEnabled() ? 15 : 0;
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side){
        return false;
    }

    public static Block getReplacementBlock(){
        return Blocks.piston;
    }

    public static int getReplacementMetadata(){
        return 6;
    }
}

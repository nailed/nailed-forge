package jk_5.nailed.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.blocks.tileentity.TileEntityElevator;
import jk_5.nailed.blocks.tileentity.TileEntityStatEmitter;
import jk_5.nailed.blocks.tileentity.TileEntityStatModifier;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
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
    private Icon[] icons;

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
        }else if(stack.getItemDamage() == 2){
            return "elevator";
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
        }else if(meta == 2){
            return new TileEntityElevator();
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister register){
        this.icons = new Icon[16];
        this.icons[0] = register.registerIcon("nailed:statemitter");
        this.icons[1] = register.registerIcon("nailed:statmodifier");
        this.icons[2] = register.registerIcon("nailed:elevator");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta){
        return this.icons[meta];
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side){
        return world.getBlockMetadata(x, y, z) != 2;
    }

    @Override
    public int damageDropped(int meta){
        return meta;
    }

    @Override
    public void getSubBlocks(int id, CreativeTabs tab, List list){
        list.add(new ItemStack(id, 1, 0));
        list.add(new ItemStack(id, 1, 1));
        list.add(new ItemStack(id, 1, 2));
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side){
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if(tile == null || !(tile instanceof TileEntityStatEmitter)) return 0;
        return ((TileEntityStatEmitter) tile).isSignalEnabled() ? 15 : 0;
    }
}

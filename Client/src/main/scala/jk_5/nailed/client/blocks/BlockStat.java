package jk_5.nailed.client.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register){
        this.icons = new IIcon[16];
        this.icons[0] = register.registerIcon("nailed:statemitter");
        this.icons[1] = register.registerIcon("nailed:statmodifier");
        this.icons[2] = register.registerIcon("nailed:elevator");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta){
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
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item item, CreativeTabs tab, List list){
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
    }
}

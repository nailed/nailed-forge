package jk_5.nailed.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.blocks.BlockMulti;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

/**
 * No description given
 *
 * @author jk-5
 */
public class ItemBlockMulti extends ItemBlock {

    private Block block;

    public ItemBlockMulti(int id){
        super(id);
        this.block = Block.blocksList[this.getBlockID()];
        this.setMaxDamage(0);
        this.setNoRepair();
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        if(this.block == null || !(this.block instanceof BlockMulti)){
            return super.getUnlocalizedName(par1ItemStack);
        }else{
            return super.getUnlocalizedName(par1ItemStack) + "." + ((BlockMulti) this.block).getUnlocalizedName(par1ItemStack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage(int meta){
        return this.block.getIcon(2, meta);
    }

    @Override
    public int getMetadata(int meta){
        return meta;
    }
}

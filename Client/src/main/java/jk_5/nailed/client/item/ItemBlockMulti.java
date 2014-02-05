package jk_5.nailed.client.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.client.blocks.BlockMulti;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * No description given
 *
 * @author jk-5
 */
public class ItemBlockMulti extends ItemBlock {

    public ItemBlockMulti(Block block){
        super(block);
        this.setMaxDamage(0);
        this.setNoRepair();
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        if(this.field_150939_a == null || !(this.field_150939_a instanceof BlockMulti)){
            return super.getUnlocalizedName(par1ItemStack);
        }else{
            return super.getUnlocalizedName(par1ItemStack) + "." + ((BlockMulti) this.field_150939_a).getUnlocalizedName(par1ItemStack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta){
        //this.block.getIcon(side, meta)
        return this.field_150939_a.getIcon(2, meta);
    }

    @Override
    public int getMetadata(int meta){
        return meta;
    }
}

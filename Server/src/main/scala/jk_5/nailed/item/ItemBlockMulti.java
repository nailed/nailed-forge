package jk_5.nailed.item;

import net.minecraft.block.*;
import net.minecraft.item.*;

import jk_5.nailed.blocks.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class ItemBlockMulti extends ItemBlock {

    public ItemBlockMulti(Block block) {
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
    public int getMetadata(int meta) {
        return meta;
    }
}

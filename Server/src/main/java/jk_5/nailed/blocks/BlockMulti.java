package jk_5.nailed.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class BlockMulti extends NailedBlock {

    protected BlockMulti(String name, Material material){
        super(name, material);
    }

    public abstract String getUnlocalizedName(ItemStack stack);
}

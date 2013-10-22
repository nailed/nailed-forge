package jk_5.nailed.blocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 * No description given
 *
 * @author jk-5
 */
public class CreativeTabNailed extends CreativeTabs {

    public CreativeTabNailed(){
        super("nailed");
    }

    @Override
    public ItemStack getIconItemStack(){
        return new ItemStack(NailedBlocks.invisibleWall, 1, 0);
    }
}

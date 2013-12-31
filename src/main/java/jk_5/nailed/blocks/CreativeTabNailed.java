package jk_5.nailed.blocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
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
    public Item getTabIconItem(){
        return null;
    }

    @Override
    public ItemStack func_151244_d(){
        return new ItemStack(NailedBlocks.invisibleWall, 1, 0);
    }
}

package jk_5.nailed.blocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

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
        return Item.func_150898_a(NailedBlocks.portal);
    }
}

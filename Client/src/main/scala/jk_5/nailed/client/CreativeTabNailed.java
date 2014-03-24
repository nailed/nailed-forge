package jk_5.nailed.client;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
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
        return Items.blaze_powder;
    }
}

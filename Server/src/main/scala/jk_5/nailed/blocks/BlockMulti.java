package jk_5.nailed.blocks;

import net.minecraft.block.material.*;
import net.minecraft.item.*;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class BlockMulti extends NailedBlock {

    protected BlockMulti(String name, Material material) {
        super(name, material);
    }

    public abstract String getUnlocalizedName(ItemStack stack);
}

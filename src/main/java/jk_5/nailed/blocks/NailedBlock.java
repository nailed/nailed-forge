package jk_5.nailed.blocks;

import jk_5.nailed.NailedModContainer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedBlock extends Block {

    public static AtomicInteger nextId = new AtomicInteger(3682);

    public NailedBlock(String name, Material material){
        super(NailedModContainer.getConfig().getTag("blocks").useBraces().getTag(name).useBraces().getTag("id").getIntValue(nextId.getAndIncrement()), material);
        this.setUnlocalizedName("nailed." + name);
    }

    @Override
    public CreativeTabs getCreativeTabToDisplayOn(){
        return NailedBlocks.creativeTab;
    }
}

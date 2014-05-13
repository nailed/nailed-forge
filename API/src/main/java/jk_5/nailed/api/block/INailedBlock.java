package jk_5.nailed.api.block;

import net.minecraft.block.Block;

/**
 * Created by matthias on 13-5-14.
 */
public interface INailedBlock {
    public Block getReplacementBlock();
    public int getReplacementMetadata();
}

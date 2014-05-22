package jk_5.nailed.api.block;

import net.minecraft.block.*;

/**
 * Created by matthias on 13-5-14.
 */
public interface INailedBlock {

    Block getReplacementBlock();
    int getReplacementMetadata();
}

package net.minecraftforge.permissions.api.context;

import net.minecraft.block.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IBlockContext extends IBlockLocationContext {

    Block getBlock();
    int getBlockMetadata();
    boolean hasTileEntity();
}

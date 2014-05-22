package jk_5.nailed.blocks.tileentity;

import io.netty.buffer.*;

import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IGuiTileEntity {

    boolean canPlayerOpenGui(Player player);

    void writeGuiData(ByteBuf buffer);
}

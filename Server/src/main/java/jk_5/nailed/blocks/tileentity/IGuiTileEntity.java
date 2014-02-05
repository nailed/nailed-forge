package jk_5.nailed.blocks.tileentity;

import io.netty.buffer.ByteBuf;
import jk_5.nailed.api.player.Player;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IGuiTileEntity {

    public boolean canPlayerOpenGui(Player player);

    public void writeGuiData(ByteBuf buffer);
}

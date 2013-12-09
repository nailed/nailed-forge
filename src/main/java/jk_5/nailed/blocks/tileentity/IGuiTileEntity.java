package jk_5.nailed.blocks.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.gui.NailedGui;
import jk_5.nailed.players.Player;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IGuiTileEntity {

    @SideOnly(Side.CLIENT)
    public NailedGui getGui();
    public boolean canPlayerOpenGui(Player player);
}

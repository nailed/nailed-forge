package jk_5.nailed.client.blocks.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.client.gui.NailedGui;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IGuiTileEntity {

    @SideOnly(Side.CLIENT)
    public NailedGui getGui();
}

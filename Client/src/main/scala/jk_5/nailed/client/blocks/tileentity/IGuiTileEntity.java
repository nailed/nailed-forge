package jk_5.nailed.client.blocks.tileentity;

import cpw.mods.fml.relauncher.*;

import jk_5.nailed.client.gui.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IGuiTileEntity {

    @SideOnly(Side.CLIENT)
    NailedGui getGui();
}

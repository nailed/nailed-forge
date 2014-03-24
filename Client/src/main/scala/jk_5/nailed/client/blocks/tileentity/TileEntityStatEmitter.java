package jk_5.nailed.client.blocks.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.client.gui.GuiStatEmitter;
import jk_5.nailed.client.gui.NailedGui;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
public class TileEntityStatEmitter extends NailedTileEntity implements IGuiTileEntity {

    @Override
    public boolean canUpdate(){
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public NailedGui getGui(){
        return new GuiStatEmitter(this);
    }
}

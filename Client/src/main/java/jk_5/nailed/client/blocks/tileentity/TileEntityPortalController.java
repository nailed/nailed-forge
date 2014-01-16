package jk_5.nailed.client.blocks.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.client.gui.GuiPortalController;
import jk_5.nailed.client.gui.NailedGui;
import lombok.Getter;

/**
 * No description given
 *
 * @author jk-5
 */
public class TileEntityPortalController extends NailedTileEntity implements IGuiTileEntity {

    public String title = "";
    public short yaw;
    public short pitch;
    @Getter private int color;

    public TileEntityPortalController(){
        this.field_145846_f = false;
        this.yaw = 0;
        this.pitch = 0;
        this.color = 0x3333FF;
    }

    @Override
    public boolean canUpdate(){
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public NailedGui getGui(){
        return new GuiPortalController(this);
    }
}

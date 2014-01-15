package jk_5.nailed.client.blocks.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.client.gui.GuiPortalController;
import jk_5.nailed.client.gui.NailedGui;
import lombok.Getter;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

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
    @Getter private String programmedName;

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
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet){
        this.yaw = packet.func_148857_g().getShort("Yaw");
        this.pitch = packet.func_148857_g().getShort("Pitch");
        this.title = packet.func_148857_g().getString("Title");
        this.color = packet.func_148857_g().getInteger("Color");
        this.programmedName = packet.func_148857_g().getString("Destination");
        this.onInventoryChanged();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public NailedGui getGui(){
        return new GuiPortalController(this);
    }

    /*public void readGuiData(MCDataInput input){
        this.setDestinationFromName(input.readString());
    }*/
}

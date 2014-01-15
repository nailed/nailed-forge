package jk_5.nailed.client.blocks.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.client.gui.GuiStatEmitter;
import jk_5.nailed.client.gui.NailedGui;
import jk_5.nailed.common.map.stat.StatMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
public class TileEntityStatEmitter extends NailedTileEntity implements IGuiTileEntity {

    @Getter private String programmedName = "";
    @Getter @Setter private StatMode mode;
    @Getter private byte pulseLength;

    public void setStatName(String statName){
        this.programmedName = statName;
    }

    /*public void readGuiData(MCDataInput input){
        this.setMode(StatMode.values()[input.readByte()]);
        this.setStatName(input.readString());
    }*/

    @Override
    public boolean canUpdate(){
        return false;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt){
        NBTTagCompound tag = pkt.func_148857_g();
        this.programmedName = tag.getString("name");
        this.mode = StatMode.values()[tag.getByte("mode")];
        this.pulseLength = tag.getByte("pulseLength");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public NailedGui getGui(){
        return new GuiStatEmitter(this);
    }
}

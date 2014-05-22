package jk_5.nailed.blocks.tileentity;

import io.netty.buffer.*;

import net.minecraft.nbt.*;

import cpw.mods.fml.common.network.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.teleport.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.blocks.*;
import jk_5.nailed.gui.*;
import jk_5.nailed.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class TileEntityPortalController extends NailedTileEntity implements IGuiTileEntity, IGuiReturnHandler, ISynchronizedTileEntity {

    public String title = "";
    public short yaw;
    public short pitch;
    private int color;
    private TeleportOptions destination;
    private String programmedName = "";

    public TileEntityPortalController() {
        this.yaw = 0;
        this.pitch = 0;
        this.color = 0x3333FF;
    }

    public void link() {
        BlockPortalController.fire(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        this.markDirty();
        this.color = 0xFF0000;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.yaw = nbttagcompound.getShort("Yaw");
        this.pitch = nbttagcompound.getShort("Pitch");
        this.setDestinationFromName(nbttagcompound.getString("Destination"));
        if(nbttagcompound.hasKey("Color")){
            this.color = nbttagcompound.getInteger("Color");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("Yaw", this.yaw);
        nbttagcompound.setShort("Pitch", this.pitch);
        nbttagcompound.setInteger("Color", this.color);
        if(this.programmedName != null){
            nbttagcompound.setString("Destination", this.programmedName);
        }
    }

    @Override
    public boolean canPlayerOpenGui(Player player) {
        if(!player.isOp()){
            player.sendChat("You need to be an OP to do that");
            return false;
        }
        return true;
    }

    @Override
    public void writeGuiData(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, this.programmedName);
    }

    @Override
    public void readGuiCloseData(ByteBuf buffer) {
        this.setDestinationFromName(ByteBufUtils.readUTF8String(buffer));
    }

    public void setDestinationFromName(String name) {
        Map map = NailedAPI.getMapLoader().getMap(name);
        if(map == null){
            return;
        }
        this.destination = map.getSpawnTeleport();
        this.programmedName = name;
        if(this.worldObj != null){
            this.link();
        }
    }

    @Override
    public void writeData(ByteBuf buffer) {
        buffer.writeInt(this.color);
    }

    public int getColor() {
        return color;
    }

    public TeleportOptions getDestination() {
        return destination;
    }

    public String getProgrammedName() {
        return programmedName;
    }
}

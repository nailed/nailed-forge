package jk_5.nailed.blocks.tileentity;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.teleport.TeleportOptions;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.blocks.BlockPortalController;
import jk_5.nailed.gui.IGuiReturnHandler;
import jk_5.nailed.util.ISynchronizedTileEntity;
import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;

/**
 * No description given
 *
 * @author jk-5
 */
public class TileEntityPortalController extends NailedTileEntity implements IGuiTileEntity, IGuiReturnHandler, ISynchronizedTileEntity {

    public String title = "";
    public short yaw;
    public short pitch;
    @Getter private int color;
    @Getter private TeleportOptions destination;
    @Getter private String programmedName = "";

    public TileEntityPortalController(){
        this.yaw = 0;
        this.pitch = 0;
        this.color = 0x3333FF;
    }

    public void link(){
        BlockPortalController.fire(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        this.markDirty();
        this.color = 0xFF0000;
    }

    @Override
    public boolean canUpdate(){
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound){
        super.readFromNBT(nbttagcompound);
        this.yaw = nbttagcompound.getShort("Yaw");
        this.pitch = nbttagcompound.getShort("Pitch");
        this.setDestinationFromName(nbttagcompound.getString("Destination"));
        if(nbttagcompound.hasKey("Color")){
            this.color = nbttagcompound.getInteger("Color");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound){
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setShort("Yaw", this.yaw);
        nbttagcompound.setShort("Pitch", this.pitch);
        nbttagcompound.setInteger("Color", this.color);
        if(this.programmedName != null) nbttagcompound.setString("Destination", this.programmedName);
    }

    @Override
    public boolean canPlayerOpenGui(Player player){
        if(!player.isOp()){
            player.sendChat("You need to be an OP to do that");
            return false;
        }
        return true;
    }

    @Override
    public void writeGuiData(ByteBuf buffer){
        ByteBufUtils.writeUTF8String(buffer, this.programmedName);
    }

    @Override
    public void readGuiCloseData(ByteBuf buffer){
        this.setDestinationFromName(ByteBufUtils.readUTF8String(buffer));
    }

    public void setDestinationFromName(String name){
        Map map = NailedAPI.getMapLoader().getMap(name);
        if(map == null) return;
        this.destination = map.getSpawnTeleport();
        this.programmedName = name;
        if(this.worldObj != null) this.link();
    }

    @Override
    public void writeData(ByteBuf buffer){
        buffer.writeInt(this.color);
    }
}

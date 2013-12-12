package jk_5.nailed.blocks.tileentity;

import jk_5.nailed.blocks.BlockPortalController;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.teleport.TeleportOptions;
import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;

/**
 * No description given
 *
 * @author jk-5
 */
public class TileEntityPortalController extends NailedTileEntity {

    public String title = "";
    public short yaw;
    public short pitch;
    @Getter
    private int color;

    public TileEntityPortalController(){
        this.tileEntityInvalid = false;
        this.yaw = 0;
        this.pitch = 0;
        this.color = 3355647;
    }

    public TeleportOptions getDestination(){
        return MapLoader.instance().getMap(0).getSpawnTeleport();
    }

    public void link(){
        BlockPortalController.fire(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        this.onInventoryChanged();
        this.color = 0xFF0000;
    }

    @Override
    public boolean canUpdate(){
        return false;   //We don't need ticks. Don't even bother giving us ticks
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound){
        super.readFromNBT(nbttagcompound);
        this.yaw = nbttagcompound.getShort("Yaw");
        this.pitch = nbttagcompound.getShort("Pitch");
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
    }

    @Override
    public Packet getDescriptionPacket(){
        Packet132TileEntityData packet = new Packet132TileEntityData();
        packet.xPosition = this.xCoord;
        packet.yPosition = this.yCoord;
        packet.zPosition = this.zCoord;
        packet.actionType = 0;
        packet.data = new NBTTagCompound();
        packet.data.setShort("Yaw", this.yaw);
        packet.data.setShort("Pitch", this.pitch);
        packet.data.setInteger("Color", this.color);
        return packet;
    }

    @Override
    public void onDataPacket(INetworkManager manager, Packet132TileEntityData packet){
        this.yaw = packet.data.getShort("Yaw");
        this.pitch = packet.data.getShort("Pitch");
        this.title = packet.data.getString("Title");
        this.color = packet.data.getInteger("Color");
        this.onInventoryChanged();
    }
}

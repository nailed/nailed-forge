package jk_5.nailed.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.blocks.BlockPortalController;
import jk_5.nailed.gui.GuiPortalController;
import jk_5.nailed.gui.NailedGui;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.teleport.TeleportOptions;
import jk_5.nailed.players.Player;
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
public class TileEntityPortalController extends NailedTileEntity implements IGuiTileEntity {

    public String title = "";
    public short yaw;
    public short pitch;
    @Getter private int color;
    @Getter private TeleportOptions destination;
    @Getter private String programmedName;

    public TileEntityPortalController(){
        this.tileEntityInvalid = false;
        this.yaw = 0;
        this.pitch = 0;
        this.color = 0x3333FF;
    }

    public void link(){
        BlockPortalController.fire(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        this.onInventoryChanged();
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
        if(this.programmedName != null) packet.data.setString("Destination", this.programmedName);
        return packet;
    }

    @Override
    public void onDataPacket(INetworkManager manager, Packet132TileEntityData packet){
        this.yaw = packet.data.getShort("Yaw");
        this.pitch = packet.data.getShort("Pitch");
        this.title = packet.data.getString("Title");
        this.color = packet.data.getInteger("Color");
        this.programmedName = packet.data.getString("Destination");
        this.onInventoryChanged();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public NailedGui getGui(){
        return new GuiPortalController(this);
    }

    @Override
    public boolean canPlayerOpenGui(Player player){
        if(!player.isOp()){
            player.sendChat("You need to be an OP to do that");
            return false;
        }
        return true;
    }

    public void setDestinationFromName(String name){
        Map map = MapLoader.instance().getMapFromName(name);
        if(map == null) return;
        this.destination = map.getSpawnTeleport();
        this.programmedName = name;
        if(this.worldObj != null) this.link();
    }

    public void readGuiData(MCDataInput input){
        this.setDestinationFromName(input.readString());
    }
}

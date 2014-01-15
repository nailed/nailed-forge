package jk_5.nailed.blocks.tileentity;

import jk_5.nailed.blocks.BlockPortalController;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.teleport.TeleportOptions;
import jk_5.nailed.players.Player;
import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
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
    @Getter private TeleportOptions destination;
    @Getter private String programmedName;

    public TileEntityPortalController(){
        this.field_145846_f = false;
        this.yaw = 0;
        this.pitch = 0;
        this.color = 0x3333FF;
    }

    public void link(){
        BlockPortalController.fire(this.field_145850_b, this.field_145851_c, this.field_145848_d, this.field_145849_e);
        this.onInventoryChanged();
        this.color = 0xFF0000;
    }

    @Override
    public boolean canUpdate(){
        return false;
    }

    @Override
    public void func_145839_a(NBTTagCompound nbttagcompound){
        super.func_145839_a(nbttagcompound);
        this.yaw = nbttagcompound.getShort("Yaw");
        this.pitch = nbttagcompound.getShort("Pitch");
        this.setDestinationFromName(nbttagcompound.getString("Destination"));
        if(nbttagcompound.hasKey("Color")){
            this.color = nbttagcompound.getInteger("Color");
        }
    }

    @Override
    public void func_145841_b(NBTTagCompound nbttagcompound){
        super.func_145841_b(nbttagcompound);
        nbttagcompound.setShort("Yaw", this.yaw);
        nbttagcompound.setShort("Pitch", this.pitch);
        nbttagcompound.setInteger("Color", this.color);
        if(this.programmedName != null) nbttagcompound.setString("Destination", this.programmedName);
    }

    @Override
    public Packet func_145844_m(){
        NBTTagCompound tag = new NBTTagCompound();
        tag.setShort("Yaw", this.yaw);
        tag.setShort("Pitch", this.pitch);
        tag.setInteger("Color", this.color);
        if(this.programmedName != null) tag.setString("Destination", this.programmedName);
        S35PacketUpdateTileEntity packet = new S35PacketUpdateTileEntity(this.field_145851_c, this.field_145848_d, this.field_145849_e, 0, tag);
        return packet;
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
        if(this.field_145850_b != null) this.link();
    }

    /*public void readGuiData(MCDataInput input){
        this.setDestinationFromName(input.readString());
    }*/
}

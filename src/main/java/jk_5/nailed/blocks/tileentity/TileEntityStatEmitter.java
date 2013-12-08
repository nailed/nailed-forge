package jk_5.nailed.blocks.tileentity;

import jk_5.nailed.map.stat.IStatTileEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
public class TileEntityStatEmitter extends TileEntity implements IStatTileEntity {

    @Getter @Setter private String statName = "";
    @Getter private boolean enabled = false;

    private int ticks = 0;

    @Override
    public void updateEntity() {
        super.updateEntity();

        ticks ++;

        if(ticks == 100){
            this.enable();
        }else if(ticks == 200){
            this.disable();
            this.ticks = 0;
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
        this.readFromNBT(pkt.data);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.statName = tag.getString("stat");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString("stat", this.statName);
    }

    @Override
    public void enable() {
        this.enabled = true;
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
    }

    @Override
    public void disable() {
        this.enabled = false;
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
    }
}

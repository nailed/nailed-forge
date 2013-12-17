package jk_5.nailed.blocks.tileentity;

import codechicken.lib.data.MCDataInput;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jk_5.nailed.gui.GuiStatEmitter;
import jk_5.nailed.gui.NailedGui;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.stat.IStatTileEntity;
import jk_5.nailed.map.stat.Stat;
import jk_5.nailed.map.stat.StatMode;
import jk_5.nailed.map.stat.StatTileEntityEvent;
import jk_5.nailed.players.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraftforge.common.MinecraftForge;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
public class TileEntityStatEmitter extends NailedTileEntity implements IStatTileEntity, IGuiTileEntity {

    @Getter private String programmedName = "";
    @Getter private boolean signalEnabled = false;
    @Getter @Setter private StatMode mode = StatMode.NORMAL;
    @Getter private Stat stat;
    private boolean needsUpdate = false;
    private boolean isLoaded = false;
    private int redstonePulseTicks = -1;
    private int pulseLength = 4;

    public void setStatName(String statName){
        this.programmedName = statName;
        if(this.worldObj == null) this.needsUpdate = true;
        else if(!this.worldObj.isRemote){
            this.stat = MapLoader.instance().getMap(this.worldObj).getStatManager().getStat(this.programmedName);
            MinecraftForge.EVENT_BUS.post(new StatTileEntityEvent.Load(this));
            this.isLoaded = true;
            if(this.stat == null){
                this.disable();
            }else{
                if(this.stat.isEnabled()) this.enable();
                else this.disable();
            }
        }
    }

    public void readGuiData(MCDataInput input){
        this.setMode(StatMode.values()[input.readByte()]);
        this.setStatName(input.readString());
    }

    @Override
    public void updateEntity(){
        if(this.needsUpdate){
            this.stat = MapLoader.instance().getMap(this.worldObj).getStatManager().getStat(this.programmedName);
            MinecraftForge.EVENT_BUS.post(new StatTileEntityEvent.Load(this));
            this.isLoaded = true;
            if(this.stat == null){
                this.disable();
            }else{
                if(this.stat.isEnabled()) this.enable();
                else this.disable();
            }
            this.needsUpdate = false;
        }
        if(!this.isLoaded){
            MinecraftForge.EVENT_BUS.post(new StatTileEntityEvent.Load(this));
            this.isLoaded = true;
        }
        if(this.redstonePulseTicks != -1){
            if(this.redstonePulseTicks == this.pulseLength){
                this.signalEnabled = false;
                this.scheduleRedstoneUpdate();
                this.redstonePulseTicks = -1;
            }else{
                this.redstonePulseTicks++;
            }
        }
    }

    @Override
    public void invalidate(){
        super.invalidate();
        if(this.isLoaded){
            MinecraftForge.EVENT_BUS.post(new StatTileEntityEvent.Unload(this));
            this.isLoaded = false;
        }
    }

    @Override
    public void onChunkUnload(){
        super.onChunkUnload();
        if(this.isLoaded){
            MinecraftForge.EVENT_BUS.post(new StatTileEntityEvent.Unload(this));
            this.isLoaded = false;
        }
    }

    @Override
    public boolean canUpdate(){
        return FMLCommonHandler.instance().getEffectiveSide().isServer();
    }

    @Override
    public Packet getDescriptionPacket(){
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt){
        this.readFromNBT(pkt.data);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        this.setStatName(tag.getString("name"));
        this.mode = StatMode.values()[tag.getByte("mode")];
        this.pulseLength = tag.getInteger("pulseLength");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        tag.setString("name", this.programmedName);
        tag.setByte("mode", (byte) this.mode.ordinal());
        tag.setInteger("pulseLength", this.pulseLength);
    }

    @Override
    public void enable(){
        if(this.stat == null && this.signalEnabled){
            this.signalEnabled = false;
            this.scheduleRedstoneUpdate();
            return;
        }
        if(this.mode == StatMode.NORMAL){
            this.signalEnabled = true;
            this.scheduleRedstoneUpdate();
        }else if(this.mode == StatMode.INVERSED){
            this.signalEnabled = false;
            this.scheduleRedstoneUpdate();
        }else if(this.mode == StatMode.PULSE_ON || this.mode == StatMode.PULSE_BOTH){
            this.signalEnabled = true;
            this.scheduleRedstoneUpdate();
            this.redstonePulseTicks = 0;
        }
    }

    @Override
    public void disable(){
        if(this.stat == null && this.signalEnabled){
            this.signalEnabled = false;
            this.scheduleRedstoneUpdate();
            return;
        }
        if(this.mode == StatMode.NORMAL){
            this.signalEnabled = false;
            this.scheduleRedstoneUpdate();
        }else if(this.mode == StatMode.INVERSED){
            this.signalEnabled = true;
            this.scheduleRedstoneUpdate();
        }else if(this.mode == StatMode.PULSE_OFF || this.mode == StatMode.PULSE_BOTH){
            this.signalEnabled = true;
            this.scheduleRedstoneUpdate();
            this.redstonePulseTicks = 0;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public NailedGui getGui(){
        return new GuiStatEmitter(this);
    }

    @Override
    public boolean canPlayerOpenGui(Player player){
        if(!player.isOp()){
            player.sendChat("You need to be an OP to do that");
            return false;
        }
        return true;
    }

    public void scheduleRedstoneUpdate(){
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
    }
}

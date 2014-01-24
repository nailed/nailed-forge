package jk_5.nailed.blocks.tileentity;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.gui.IGuiReturnHandler;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.stat.IStatTileEntity;
import jk_5.nailed.map.stat.Stat;
import jk_5.nailed.map.stat.StatManager;
import jk_5.nailed.map.stat.StatMode;
import jk_5.nailed.players.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
public class TileEntityStatEmitter extends NailedTileEntity implements IStatTileEntity, IGuiTileEntity, IGuiReturnHandler {

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
        if(this.field_145850_b == null) this.needsUpdate = true;
        else if(!this.field_145850_b.isRemote){
            StatManager manager = MapLoader.instance().getMap(this.field_145850_b).getStatManager();
            this.stat = manager.getStat(this.programmedName);
            manager.registerStatTile(this);
            this.isLoaded = true;
            if(this.stat == null){
                this.disable();
            }else{
                if(this.stat.isEnabled()) this.enable();
                else this.disable();
            }
        }
    }

    @Override
    public void func_145845_h(){
        if(this.needsUpdate){
            StatManager manager = MapLoader.instance().getMap(this.field_145850_b).getStatManager();
            this.stat = manager.getStat(this.programmedName);
            manager.registerStatTile(this);
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
            MapLoader.instance().getMap(this.field_145850_b).getStatManager().registerStatTile(this);
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
    public void func_145843_s(){
        super.func_145843_s();
        if(this.isLoaded){
            MapLoader.instance().getMap(this.field_145850_b).getStatManager().unloadStatTile(this);
            this.isLoaded = false;
        }
    }

    @Override
    public void onChunkUnload(){
        super.onChunkUnload();
        if(this.isLoaded){
            MapLoader.instance().getMap(this.field_145850_b).getStatManager().unloadStatTile(this);
            this.isLoaded = false;
        }
    }

    @Override
    public boolean canUpdate(){
        return FMLCommonHandler.instance().getEffectiveSide().isServer();
    }

    @Override
    public void func_145839_a(NBTTagCompound tag){
        super.func_145839_a(tag);
        this.setStatName(tag.getString("name"));
        this.mode = StatMode.values()[tag.getByte("mode")];
        this.pulseLength = tag.getInteger("pulseLength");
    }

    @Override
    public void func_145841_b(NBTTagCompound tag){
        super.func_145841_b(tag);
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
        buffer.writeByte(this.mode.ordinal());
        buffer.writeByte(this.pulseLength);
    }

    @Override
    public void readGuiCloseData(ByteBuf buffer){
        this.setStatName(ByteBufUtils.readUTF8String(buffer));
        this.setMode(StatMode.values()[buffer.readByte()]);
        this.pulseLength = buffer.readByte();
    }

    public void scheduleRedstoneUpdate(){
        this.field_145850_b.func_147459_d(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.field_145854_h);
    }
}

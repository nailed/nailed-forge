package jk_5.nailed.map.script;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.Unpooled;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.network.NailedNetworkHandler;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * No description given
 *
 * @author jk-5
 */
public class TerminalSynchronizer {

    @Setter protected Map owner;
    @Getter protected Terminal terminal = null;
    @Getter private boolean terminalReady = true;
    @Getter private boolean terminalChanged = false;

    public TerminalSynchronizer(Map owner){
        this.owner = owner;
    }

    public TerminalSynchronizer(Map owner, int terminalWidth, int terminalHeight){
        this.owner = owner;
        this.terminal = new Terminal(terminalWidth, terminalHeight);
    }

    public void copyFrom(Terminal terminal){
        this.terminal = terminal;
        this.terminalChanged = true;
    }

    public void resize(int width, int height){
        if(this.terminal == null){
            this.terminal = new Terminal(width, height);
            this.terminalChanged = true;
        }else{
            this.terminal.resize(width, height);
        }
    }

    public void delete(){
        if(this.terminal != null){
            this.terminal = null;
            this.terminalChanged = true;
        }
    }

    public void update(){
        ScriptPacket packet = null;
        if(this.terminal == null){
            if(this.terminalChanged){
                packet = createTerminalChangedPacket(true);
            }
            this.terminalChanged = false;
        }else{
            synchronized(this.terminal){
                if(this.terminalChanged || (this.terminal != null && this.terminal.isChanged())){
                    packet = createTerminalChangedPacket(this.terminalChanged);
                    if(this.terminal != null){
                        this.terminal.clearChanged();
                    }
                    this.terminalChanged = false;
                }
            }
        }
        if(packet != null){
            NailedNetworkHandler.sendPacketToAllPlayersInDimension(packet, this.owner.getID());
        }
    }

    public void writeToNBT(NBTTagCompound nbttagcompound){
    }

    public void readFromNBT(NBTTagCompound nbttagcompound){
    }

    public static void writeTerminalToNBT(NBTTagCompound nbttagcompound, Terminal term){
        nbttagcompound.setInteger("term_cursorX", term.getCursorX());
        nbttagcompound.setInteger("term_cursorY", term.getCursorY());
        nbttagcompound.setBoolean("term_cursorBlink", term.isCursorBlink());
        nbttagcompound.setInteger("term_textColor", term.getTextColor());
        nbttagcompound.setInteger("term_bgColor", term.getBackgroundColor());
        for(int n = 0; n < term.getHeight(); n++){
            nbttagcompound.setString("term_line_" + n, term.getLine(n));
            nbttagcompound.setString("term_colorline_" + n, term.getColorLine(n));
        }
    }

    public static void readTerminalFromNBT(NBTTagCompound nbttagcompound, Terminal term){
        term.clear();
        for(int n = 0; n < term.getHeight(); n++){
            if(nbttagcompound.hasKey("term_line_" + n)){
                term.setLine(n, nbttagcompound.getString("term_line_" + n), nbttagcompound.getString("term_colorline_" + n));
            }
        }
        term.setCursorPos(nbttagcompound.getInteger("term_cursorX"), nbttagcompound.getInteger("term_cursorY"));
        term.setCursorBlink(nbttagcompound.getBoolean("term_cursorBlink"));
        term.setTextColor(nbttagcompound.getInteger("term_textColor"));
        term.setBackgroundColor(nbttagcompound.getInteger("term_bgColor"));
    }

    public boolean isDestroyed(){
        return false;
    }

    private ScriptPacket createTerminalChangedPacket(boolean _includeAllText){
        if(this.terminal == null){
            return new ScriptPacket.RemoveTerminal();
        }

        synchronized(this.terminal){
            int width = this.terminal.getWidth();
            int height = this.terminal.getHeight();
            boolean[] lineChanged = this.terminal.getLineChanged();
            boolean[] colorLineChanged = this.terminal.getColorLineChanged();

            int lineChangeMask = this.terminal.isCursorBlink() ? 1 : 0;
            int lineChangeMask2 = 0;
            int lineChangeMask3 = 0;
            int colorLineChangeMask = this.terminal.isCursorBlink() ? 1 : 0;
            int colorLineChangeMask2 = 0;
            int colorLineChangeMask3 = 0;

            for(int y = 0; y < height; y++){
                if(lineChanged[y] || _includeAllText){
                    if(y < 30)
                        lineChangeMask += (1 << y + 1);
                    else if(y < 60)
                        lineChangeMask2 += (1 << y - 30);
                    else{
                        lineChangeMask3 += (1 << y - 60);
                    }
                }
                if(colorLineChanged[y] || _includeAllText){
                    if(y < 30)
                        colorLineChangeMask += (1 << y + 1);
                    else if(y < 60)
                        colorLineChangeMask2 += (1 << y - 30);
                    else{
                        colorLineChangeMask3 += (1 << y - 60);
                    }
                }

            }

            ScriptPacket.UpdateTerminal packet = new ScriptPacket.UpdateTerminal();

            int colors = ((this.terminal.getBackgroundColor() & 0xF) << 4) + (this.terminal.getTextColor() & 0xF);
            if(lineChangeMask2 != 0 || colorLineChangeMask2 != 0 || lineChangeMask3 != 0 || colorLineChangeMask3 != 0){
                packet.additionalMasks = true;
                packet.width = width;
                packet.height = height;
                packet.cursorX = this.terminal.getCursorX();
                packet.cursorY = this.terminal.getCursorY();
                packet.colors = colors;
                packet.lineChangeMask = lineChangeMask;
                packet.lineChangeMask1 = lineChangeMask2;
                packet.lineChangeMask2 = lineChangeMask3;
                packet.colorLineChangeMask = colorLineChangeMask;
                packet.colorLineChangeMask1 = colorLineChangeMask2;
                packet.colorLineChangeMask2 = colorLineChangeMask3;
            }else{
                packet.additionalMasks = false;
                packet.width = width;
                packet.height = height;
                packet.cursorX = this.terminal.getCursorX();
                packet.cursorY = this.terminal.getCursorY();
                packet.colors = colors;
                packet.lineChangeMask = lineChangeMask;
                packet.colorLineChangeMask = colorLineChangeMask;
            }

            packet.data = Unpooled.buffer();
            for(int y = 0; y < this.terminal.getHeight(); y++){
                if(lineChanged[y] || _includeAllText){
                    ByteBufUtils.writeUTF8String(packet.data, this.terminal.getLine(y).replaceAll(" +$", ""));
                }
            }
            for(int y = 0; y < this.terminal.getHeight(); y++){
                if(colorLineChanged[y] || _includeAllText){
                    ByteBufUtils.writeUTF8String(packet.data, this.terminal.getColorLine(y).replaceAll(" +$", ""));
                }
            }
            return packet;
        }
    }

    public void updateClient(EntityPlayer player){
        ScriptPacket packet = this.createTerminalChangedPacket(true);
        NailedNetworkHandler.sendPacketToPlayer(packet, player);
    }
}

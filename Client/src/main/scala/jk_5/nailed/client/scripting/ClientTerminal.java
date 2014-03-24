package jk_5.nailed.client.scripting;

import io.netty.buffer.ByteBuf;
import jk_5.nailed.map.script.ITerminal;
import jk_5.nailed.map.script.Terminal;
import lombok.Getter;

/**
 * No description given
 *
 * @author jk-5
 */
public class ClientTerminal implements ITerminal {

    @Getter private Terminal terminal = null;
    private boolean terminalChanged = false;

    public boolean pollChanged(){
        if(this.terminalChanged || (this.terminal != null && this.terminal.isChanged())){
            if(this.terminal != null){
                this.terminal.clearChanged();
            }
            this.terminalChanged = false;
            return true;
        }
        return false;
    }

    public void readData(ByteBuf buffer){
        boolean hasTerminal = buffer.readBoolean();
        if(hasTerminal){
            this.resizeTerminal(buffer.readInt(), buffer.readInt());
            this.terminal.readData(buffer);
        }else{
            deleteTerminal();
        }
    }

    private void resizeTerminal(int width, int height){
        if(this.terminal == null){
            this.terminal = new Terminal(width, height);
            this.terminalChanged = true;
        }else{
            this.terminal.resize(width, height);
        }
    }

    private void deleteTerminal(){
        if(this.terminal != null){
            this.terminal = null;
            this.terminalChanged = true;
        }
    }
}

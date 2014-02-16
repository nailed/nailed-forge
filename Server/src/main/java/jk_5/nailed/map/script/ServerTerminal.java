package jk_5.nailed.map.script;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
public class ServerTerminal implements ITerminal {

    @Getter private Terminal terminal = null;
    public boolean terminalChanged = false;

    public ServerTerminal(int width, int height){
        this.terminal = new Terminal(width, height);
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

    public void writeData(ByteBuf buffer){
        if(this.terminal != null){
            buffer.writeBoolean(true);
            buffer.writeInt(this.terminal.getWidth());
            buffer.writeInt(this.terminal.getHeight());
            this.terminal.writeData(buffer);
        }else{
            buffer.writeBoolean(false);
        }
    }
}

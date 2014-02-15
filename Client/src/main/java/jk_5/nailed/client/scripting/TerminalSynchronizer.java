package jk_5.nailed.client.scripting;

import jk_5.nailed.client.network.ClientNetworkHandler;
import jk_5.nailed.map.script.ScriptPacket;
import jk_5.nailed.map.script.Terminal;
import lombok.Getter;

/**
 * No description given
 *
 * @author jk-5
 */
public class TerminalSynchronizer {

    @Getter protected Terminal terminal = null;
    @Getter private boolean terminalChanged = false;

    public TerminalSynchronizer(int terminalWidth, int terminalHeight){
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

    public void requestUpdate(){
        ScriptPacket packet = new ScriptPacket.RequestTerminalUpdate();
        ClientNetworkHandler.sendPacketToServer(packet);
    }
}

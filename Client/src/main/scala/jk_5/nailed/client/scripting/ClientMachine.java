package jk_5.nailed.client.scripting;

import io.netty.buffer.*;

import jk_5.nailed.client.network.*;
import jk_5.nailed.map.script.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class ClientMachine {

    private final int instanceId;
    private Terminal terminal = null;

    public ClientMachine(int instanceId){
        this.instanceId = instanceId;
    }

    public void turnOn(){
        ClientNetworkHandler.sendPacketToServer(new ScriptPacket.StateEvent(this.instanceId, 0));
    }

    public void shutdown(){
        ClientNetworkHandler.sendPacketToServer(new ScriptPacket.StateEvent(this.instanceId, 1));
    }

    public void reboot(){
        ClientNetworkHandler.sendPacketToServer(new ScriptPacket.StateEvent(this.instanceId, 2));
    }

    public void queueEvent(String event, Object... arguments){
        ClientNetworkHandler.sendPacketToServer(new ScriptPacket.QueueEvent(this.instanceId, event, arguments));
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
        }else{
            this.terminal.resize(width, height);
        }
    }

    private void deleteTerminal(){
        this.terminal = null;
    }

    public Terminal getTerminal() {
        return terminal;
    }
}

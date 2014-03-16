package jk_5.nailed.client.scripting;

import io.netty.buffer.ByteBuf;
import jk_5.nailed.client.network.ClientNetworkHandler;
import jk_5.nailed.map.script.IMachine;
import jk_5.nailed.map.script.ScriptPacket;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class ClientMachine extends ClientTerminal implements IMachine {

    private final int instanceId;
    @Getter private int id;
    @Getter private boolean on;
    private boolean blinking;
    private boolean changed;

    public ClientMachine(int instanceId){
        this.instanceId = instanceId;
        this.on = false;
        this.id = -1;
        this.blinking = false;
        this.changed = false;
    }

    public boolean pollChanged(){
        if(this.changed){
            this.changed = false;
            return true;
        }
        return false;
    }

    public boolean isCursorDisplayed(){
        return this.on && this.blinking;
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

    @Override
    public World getWorld(){
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public double getTimeOfDay(){
        return 0;
    }

    @Override
    public int getDay(){
        return 0;
    }

    public void readData(ByteBuf buffer){
        super.readData(buffer);
        this.id = buffer.readInt();
        this.on = buffer.readBoolean();
        this.blinking = buffer.readBoolean();
        this.changed = true;
    }
}

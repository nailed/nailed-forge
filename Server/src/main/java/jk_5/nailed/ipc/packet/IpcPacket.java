package jk_5.nailed.ipc.packet;

import com.google.gson.JsonObject;
import jk_5.nailed.ipc.PacketManager;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class IpcPacket {

    public abstract void read(JsonObject json);
    public abstract void write(JsonObject json);
    public abstract void processPacket();

    public boolean hasData(){
        return true;
    }

    public boolean canBeHandledASync(){
        return true;
    }

    public final String getPacketName(){
        return PacketManager.getName(this.getClass());
    }
}

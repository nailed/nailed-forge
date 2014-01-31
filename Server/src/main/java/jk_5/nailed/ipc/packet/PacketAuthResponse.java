package jk_5.nailed.ipc.packet;

import com.google.gson.JsonObject;
import jk_5.nailed.ipc.IpcManager;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketAuthResponse extends IpcPacket {

    public boolean success;

    @Override
    public void read(JsonObject json){
        this.success = json.get("success").getAsBoolean();
    }

    @Override
    public void write(JsonObject json){

    }

    @Override
    public void processPacket(){
        IpcManager.instance().sendPacket(new PacketInitConnection());
    }
}

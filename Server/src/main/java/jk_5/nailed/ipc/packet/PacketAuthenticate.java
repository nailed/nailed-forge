package jk_5.nailed.ipc.packet;

import com.google.gson.JsonObject;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketAuthenticate extends IpcPacket {

    @Override
    public void read(JsonObject json){

    }

    @Override
    public void write(JsonObject json){
        json.addProperty("type", "server");
    }

    @Override
    public void processPacket(){

    }
}

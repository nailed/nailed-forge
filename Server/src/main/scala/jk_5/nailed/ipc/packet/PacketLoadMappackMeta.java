package jk_5.nailed.ipc.packet;

import com.google.gson.*;

import io.netty.buffer.*;

import jk_5.nailed.api.*;
import jk_5.nailed.ipc.*;
import jk_5.nailed.ipc.mappack.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketLoadMappackMeta extends IpcPacket {

    public String id;
    public JsonObject data;

    public PacketLoadMappackMeta() {

    }

    public PacketLoadMappackMeta(String id, JsonObject data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public void encode(ByteBuf buffer) {

    }

    @Override
    public void decode(ByteBuf buffer) {
        this.id = PacketUtils.readString(buffer);
        this.data = new JsonParser().parse(PacketUtils.readString(buffer)).getAsJsonObject();
    }

    @Override
    public void processPacket() {
        IpcMappack mappack = new IpcMappack(this.data);
        IpcMappackRegistry.addMappack(mappack);
        NailedAPI.getMapLoader().createMapServer(mappack, null);
    }
}

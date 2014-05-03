package jk_5.nailed.ipc.packet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.ipc.PacketUtils;
import jk_5.nailed.ipc.mappack.IpcMappack;
import jk_5.nailed.ipc.mappack.IpcMappackRegistry;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketLoadMappackMeta extends IpcPacket {

    public String id;
    public JsonObject data;

    @Override
    public void encode(ByteBuf buffer){

    }

    @Override
    public void decode(ByteBuf buffer){
        this.id = PacketUtils.readString(buffer);
        this.data = new JsonParser().parse(PacketUtils.readString(buffer)).getAsJsonObject();
    }

    @Override
    public void processPacket(){
        IpcMappackRegistry.addMappack(new IpcMappack(this.data));
    }
}

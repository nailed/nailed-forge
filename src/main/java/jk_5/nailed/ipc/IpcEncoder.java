package jk_5.nailed.ipc;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import jk_5.nailed.ipc.packet.IpcPacket;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
@ChannelHandler.Sharable
public class IpcEncoder extends MessageToMessageEncoder<IpcPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, IpcPacket msg, List<Object> out) throws Exception {
        JsonObject data = new JsonObject();
        msg.write(data);
        JsonObject packetData = new JsonObject();
        packetData.addProperty("name", msg.getPacketName());
        if(msg.hasData()) packetData.add("data", data);
        out.add(data);
    }
}

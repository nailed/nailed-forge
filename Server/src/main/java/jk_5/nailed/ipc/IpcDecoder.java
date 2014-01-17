package jk_5.nailed.ipc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jk_5.nailed.ipc.packet.IpcPacket;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
@ChannelHandler.Sharable
public class IpcDecoder extends MessageToMessageDecoder<TextWebSocketFrame> {

    @Override
    protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame msg, List<Object> out) throws Exception {
        JsonObject obj = new JsonParser().parse(msg.text()).getAsJsonObject();
        if(!obj.has("name")) return;
        IpcPacket packet = PacketManager.getPacket(obj.get("name").getAsString());
        if(packet == null) return;
        if(obj.has("data")) packet.read(obj.getAsJsonObject("data"));
        out.add(packet);
    }
}

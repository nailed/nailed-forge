package jk_5.nailed.ipc.codec;

import gnu.trove.map.hash.TByteObjectHashMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import jk_5.nailed.ipc.packet.IpcPacket;
import jk_5.nailed.ipc.packet.PacketInitConnection;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketCodec extends ByteToMessageCodec<IpcPacket> {

    private final TByteObjectHashMap<Class<? extends IpcPacket>> idToClass = new TByteObjectHashMap<Class<? extends IpcPacket>>();
    private final TObjectByteHashMap<Class<? extends IpcPacket>> classToId = new TObjectByteHashMap<Class<? extends IpcPacket>>();

    public PacketCodec(){
        this.registerPacket(1, PacketInitConnection.class);
    }

    private PacketCodec registerPacket(int id, Class<? extends IpcPacket> packet){
        this.idToClass.put((byte) id, packet);
        this.classToId.put(packet, (byte) id);
        return this;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, IpcPacket msg, ByteBuf out) throws Exception{
        Class<? extends IpcPacket> cl = msg.getClass();
        if(!this.classToId.containsKey(cl)){
            throw new UnsupportedOperationException("Trying to send an unregistered packet (" + cl.getSimpleName() + ")");
        }
        int id = this.classToId.get(cl);
        out.writeByte(id);
        msg.encode(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception{
        byte id = in.readByte();
        if(!this.idToClass.containsKey(id)){
            throw new UnsupportedOperationException("Received an unknown packet (id: " + id + ")");
        }
        IpcPacket packet = this.idToClass.get(id).newInstance();
        packet.decode(in);
        out.add(packet);
    }
}
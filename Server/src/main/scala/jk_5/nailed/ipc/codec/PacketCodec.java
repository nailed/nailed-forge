package jk_5.nailed.ipc.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import jk_5.nailed.ipc.packet.IpcPacket;
import jk_5.nailed.ipc.packet.PacketChat;
import jk_5.nailed.ipc.packet.PacketChatIn;
import jk_5.nailed.ipc.packet.PacketCheckAccount;
import jk_5.nailed.ipc.packet.PacketCreateAccount;
import jk_5.nailed.ipc.packet.PacketIdentify;
import jk_5.nailed.ipc.packet.PacketInitConnection;
import jk_5.nailed.ipc.packet.PacketKill;
import jk_5.nailed.ipc.packet.PacketListMappacks;
import jk_5.nailed.ipc.packet.PacketLoadMappackMeta;
import jk_5.nailed.ipc.packet.PacketLoginPlayer;
import jk_5.nailed.ipc.packet.PacketLoginResponse;
import jk_5.nailed.ipc.packet.PacketPlayerDeath;
import jk_5.nailed.ipc.packet.PacketPlayerJoin;
import jk_5.nailed.ipc.packet.PacketPlayerLeave;
import jk_5.nailed.ipc.packet.PacketPromptLogin;
import jk_5.nailed.ipc.packet.PacketRequestMappackLoad;
import jk_5.nailed.ipc.packet.PacketUserdata;

import gnu.trove.map.hash.TByteObjectHashMap;
import gnu.trove.map.hash.TObjectByteHashMap;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketCodec extends ByteToMessageCodec<IpcPacket> {

    private final TByteObjectHashMap<Class<? extends IpcPacket>> idToClass = new TByteObjectHashMap<Class<? extends IpcPacket>>();
    private final TObjectByteHashMap<Class<? extends IpcPacket>> classToId = new TObjectByteHashMap<Class<? extends IpcPacket>>();

    public PacketCodec() {
        this.registerPacket(0, PacketIdentify.class);
        this.registerPacket(1, PacketInitConnection.class);
        this.registerPacket(2, PacketPlayerJoin.class);
        this.registerPacket(3, PacketPlayerLeave.class);
        this.registerPacket(4, PacketPlayerDeath.class);
        this.registerPacket(5, PacketKill.class);
        this.registerPacket(6, PacketPromptLogin.class);
        this.registerPacket(7, PacketLoginPlayer.class);
        this.registerPacket(8, PacketLoginResponse.class);
        this.registerPacket(9, PacketCheckAccount.class);
        this.registerPacket(10, PacketCreateAccount.class);
        this.registerPacket(11, PacketUserdata.class);
        this.registerPacket(12, PacketLoadMappackMeta.class);
        this.registerPacket(13, PacketChatIn.class);
        this.registerPacket(14, PacketChat.class);
        this.registerPacket(15, PacketListMappacks.class);
        this.registerPacket(16, PacketRequestMappackLoad.class);
    }

    private PacketCodec registerPacket(int id, Class<? extends IpcPacket> packet) {
        this.idToClass.put((byte) id, packet);
        this.classToId.put(packet, (byte) id);
        return this;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, IpcPacket msg, ByteBuf out) throws Exception {
        Class<? extends IpcPacket> cl = msg.getClass();
        if(!this.classToId.containsKey(cl)){
            throw new UnsupportedOperationException("Trying to send an unregistered packet (" + cl.getSimpleName() + ")");
        }
        int id = this.classToId.get(cl);
        out.writeByte(id);
        msg.encode(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte id = in.readByte();
        if(!this.idToClass.containsKey(id)){
            throw new UnsupportedOperationException("Received an unknown packet (id: " + id + ")");
        }
        IpcPacket packet = this.idToClass.get(id).newInstance();
        packet.decode(in);
        out.add(packet);
    }
}

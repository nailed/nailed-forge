package jk_5.nailed.ipc.packet;

import io.netty.buffer.ByteBuf;

import jk_5.nailed.ipc.PacketUtils;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketRequestMappackLoad extends IpcPacket {

    private String name;

    public PacketRequestMappackLoad() {

    }

    public PacketRequestMappackLoad(String name) {
        this.name = name;
    }

    @Override
    public void encode(ByteBuf buffer) {
        PacketUtils.writeString(this.name, buffer);
    }

    @Override
    public void decode(ByteBuf buffer) {

    }

    @Override
    public void processPacket() {

    }
}

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
    private boolean autoload = true;

    public PacketRequestMappackLoad() {

    }

    public PacketRequestMappackLoad(String name) {
        this.name = name;
    }

    public PacketRequestMappackLoad(String name, boolean autoload) {
        this.name = name;
        this.autoload = autoload;
    }

    @Override
    public void encode(ByteBuf buffer) {
        PacketUtils.writeString(this.name, buffer);
        buffer.writeBoolean(this.autoload);
    }

    @Override
    public void decode(ByteBuf buffer) {

    }

    @Override
    public void processPacket() {

    }
}

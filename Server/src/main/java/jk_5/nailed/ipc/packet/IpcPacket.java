package jk_5.nailed.ipc.packet;

import io.netty.buffer.ByteBuf;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class IpcPacket {

    public abstract void encode(ByteBuf buffer);
    public abstract void decode(ByteBuf buffer);
    public abstract void processPacket();
}

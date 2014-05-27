package jk_5.nailed.ipc.packet;

import io.netty.buffer.*;

import jk_5.nailed.ipc.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketChatIn extends IpcPacket {

    public enum Target {
        GLOBAL,
        MAP,
        TEAM,
        PLAYER
    }

    public String playerId;
    public String message;
    public Target targetType;
    public String target;

    @Override
    public void encode(ByteBuf buffer) {
        PacketUtils.writeString(this.playerId, buffer);
        PacketUtils.writeString(this.message, buffer);
        buffer.writeByte(this.targetType.ordinal());
        PacketUtils.writeString(this.target, buffer);
    }

    @Override
    public void decode(ByteBuf buffer) {
        //NOOP
    }

    @Override
    public void processPacket() {

    }
}

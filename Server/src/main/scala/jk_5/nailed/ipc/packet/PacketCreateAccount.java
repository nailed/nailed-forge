package jk_5.nailed.ipc.packet;

import io.netty.buffer.*;

import jk_5.nailed.ipc.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketCreateAccount extends IpcPacket {

    public String playerId;
    public String username;
    public String email;
    public String name;
    public String password;

    public PacketCreateAccount() {

    }

    public PacketCreateAccount(String playerId, String username, String email, String name, String password) {
        this.playerId = playerId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.password = password;
    }

    @Override
    public void encode(ByteBuf buffer) {
        PacketUtils.writeString(this.playerId, buffer);
        PacketUtils.writeString(this.username, buffer);
        PacketUtils.writeString(this.email, buffer);
        PacketUtils.writeString(this.name, buffer);
        PacketUtils.writeString(this.password, buffer);
    }

    @Override
    public void decode(ByteBuf buffer) {

    }

    @Override
    public void processPacket() {

    }
}

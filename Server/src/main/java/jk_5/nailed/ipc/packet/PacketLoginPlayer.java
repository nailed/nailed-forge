package jk_5.nailed.ipc.packet;

import io.netty.buffer.*;

import jk_5.nailed.api.player.*;
import jk_5.nailed.ipc.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketLoginPlayer extends IpcPacket {

    public Player player;
    public String username;
    public String password;

    public PacketLoginPlayer() {

    }

    public PacketLoginPlayer(Player player, String username, String password) {
        this.player = player;
        this.username = username;
        this.password = password;
    }

    @Override
    public void encode(ByteBuf buffer) {
        PacketUtils.writeString(player.getId(), buffer);
        PacketUtils.writeString(this.username, buffer);
        PacketUtils.writeString(this.password, buffer);
    }

    @Override
    public void decode(ByteBuf buffer) {

    }

    @Override
    public void processPacket() {

    }
}

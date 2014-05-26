package jk_5.nailed.ipc.packet;

import io.netty.buffer.*;

import jk_5.nailed.api.player.*;
import jk_5.nailed.ipc.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketPlayerDeath extends IpcPacket {

    private Player player;
    private String cause;

    public PacketPlayerDeath() {

    }

    public PacketPlayerDeath(Player player, String cause) {
        this.player = player;
        this.cause = cause;
    }

    @Override
    public void encode(ByteBuf buffer) {
        PacketUtils.writeString(this.player.getGameProfile().getId(), buffer);
        PacketUtils.writeString(this.cause, buffer);
    }

    @Override
    public void decode(ByteBuf buffer) {

    }

    @Override
    public void processPacket() {

    }
}

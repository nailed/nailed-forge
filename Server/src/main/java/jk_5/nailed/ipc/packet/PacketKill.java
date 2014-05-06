package jk_5.nailed.ipc.packet;

import io.netty.buffer.ByteBuf;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.ipc.PacketUtils;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketKill extends IpcPacket {

    private Player killer;
    private Player victim;

    public PacketKill() {

    }

    public PacketKill(Player killer, Player victim) {
        this.killer = killer;
        this.victim = victim;
    }

    @Override
    public void encode(ByteBuf buffer){
        PacketUtils.writeString(killer.getGameProfile().getId(), buffer);
        PacketUtils.writeString(victim.getGameProfile().getId(), buffer);
    }

    @Override
    public void decode(ByteBuf buffer){

    }

    @Override
    public void processPacket() {

    }
}

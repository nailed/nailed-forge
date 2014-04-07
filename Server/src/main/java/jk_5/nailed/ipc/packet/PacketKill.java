package jk_5.nailed.ipc.packet;

import io.netty.buffer.ByteBuf;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.ipc.PacketUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketKill extends IpcPacket {

    private Player killer;
    private Player victim;

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

package jk_5.nailed.ipc.packet;

import io.netty.buffer.ByteBuf;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.ipc.PacketUtils;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketPlayerLeave extends IpcPacket {

    private Player player;

    public PacketPlayerLeave() {

    }

    public PacketPlayerLeave(Player player) {
        this.player = player;
    }

    @Override
    public void encode(ByteBuf buffer){
        PacketUtils.writeString(this.player.getGameProfile().getId(), buffer);
    }

    @Override
    public void decode(ByteBuf buffer){

    }

    @Override
    public void processPacket() {

    }
}

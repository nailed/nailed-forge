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
public class PacketPlayerJoin extends IpcPacket {

    private Player player;
    private String ip;

    @Override
    public void encode(ByteBuf buffer){
        PacketUtils.writeString(this.player.getGameProfile().getId(), buffer);
        PacketUtils.writeString(this.player.getUsername(), buffer);
        PacketUtils.writeString(this.ip, buffer);
    }

    @Override
    public void decode(ByteBuf buffer){

    }

    @Override
    public void processPacket() {

    }
}

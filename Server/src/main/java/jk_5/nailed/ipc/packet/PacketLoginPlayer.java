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
public class PacketLoginPlayer extends IpcPacket {

    public Player player;
    public String username;
    public String password;

    @Override
    public void encode(ByteBuf buffer){
        PacketUtils.writeString(player.getId(), buffer);
        PacketUtils.writeString(this.username, buffer);
        PacketUtils.writeString(this.password, buffer);
    }

    @Override
    public void decode(ByteBuf buffer){

    }

    @Override
    public void processPacket(){

    }
}

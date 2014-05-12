package jk_5.nailed.ipc.packet;

import io.netty.buffer.ByteBuf;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.ipc.PacketUtils;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketInitConnection extends IpcPacket {

    @Override
    public void encode(ByteBuf buffer){
        List<Player> players = NailedAPI.getPlayerRegistry().getOnlinePlayers();
        PacketUtils.writeString("minecraft.jk-5.tk:25566", buffer);
        PacketUtils.writeVarInt(players.size(), buffer);
        for(Player player : players){
            PacketUtils.writeString(player.getId(), buffer);
            PacketUtils.writeString(player.getUsername(), buffer);
        }
    }

    @Override
    public void decode(ByteBuf buffer){

    }

    @Override
    public void processPacket() {

    }
}

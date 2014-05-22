package jk_5.nailed.ipc.packet;

import java.util.*;

import io.netty.buffer.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.ipc.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketInitConnection extends IpcPacket {

    @Override
    public void encode(ByteBuf buffer) {
        List<Player> players = NailedAPI.getPlayerRegistry().getOnlinePlayers();
        PacketUtils.writeString("minecraft.jk-5.tk:25566", buffer);
        PacketUtils.writeVarInt(players.size(), buffer);
        for(Player player : players){
            PacketUtils.writeString(player.getId(), buffer);
            PacketUtils.writeString(player.getUsername(), buffer);
        }
    }

    @Override
    public void decode(ByteBuf buffer) {

    }

    @Override
    public void processPacket() {

    }
}

package jk_5.nailed.ipc.packet;

import io.netty.buffer.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.ipc.*;
import jk_5.nailed.network.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketPromptLogin extends IpcPacket {

    private String playerId;

    public PacketPromptLogin() {

    }

    public PacketPromptLogin(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public void encode(ByteBuf buffer) {

    }

    @Override
    public void decode(ByteBuf buffer) {
        this.playerId = PacketUtils.readString(buffer);
    }

    @Override
    public void processPacket() {
        Player player = NailedAPI.getPlayerRegistry().getPlayerById(this.playerId);
        if(player != null){
            NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.DisplayLogin(), player.getEntity());
        }
    }
}

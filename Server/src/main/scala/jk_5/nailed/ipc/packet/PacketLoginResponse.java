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
public class PacketLoginResponse extends IpcPacket {

    private String playerId;
    private int state; //0 = OK, 1 = Wrong Username, 2 = Wrong Password, 3 = Unknown Player

    public PacketLoginResponse() {

    }

    public PacketLoginResponse(String playerId, int state) {
        this.playerId = playerId;
        this.state = state;
    }

    @Override
    public void encode(ByteBuf buffer) {

    }

    @Override
    public void decode(ByteBuf buffer) {
        boolean hasPlayer = buffer.readBoolean();
        if(hasPlayer){
            this.playerId = PacketUtils.readString(buffer);
        }
        this.state = buffer.readByte();
    }

    @Override
    public void processPacket() {
        Player player = NailedAPI.getPlayerRegistry().getPlayerById(this.playerId);
        NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.LoginResponse(this.state), player.getEntity());
    }
}

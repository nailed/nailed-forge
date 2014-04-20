package jk_5.nailed.ipc.packet;

import io.netty.buffer.ByteBuf;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.ipc.PacketUtils;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketLoginResponse extends IpcPacket {

    private String playerId;
    private int state; //0 = OK, 1 = Wrong Username, 2 = Wrong Password, 3 = Unknown Player

    @Override
    public void encode(ByteBuf buffer){

    }

    @Override
    public void decode(ByteBuf buffer){
        boolean hasPlayer = buffer.readBoolean();
        if(hasPlayer) this.playerId = PacketUtils.readString(buffer);
        this.state = buffer.readByte();
    }

    @Override
    public void processPacket(){
        Player player = NailedAPI.getPlayerRegistry().getPlayerById(this.playerId);
        NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.LoginResponse(this.state), player.getEntity());
    }
}

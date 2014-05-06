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
public class PacketPromptLogin extends IpcPacket {

    private String playerId;

    public PacketPromptLogin() {

    }

    public PacketPromptLogin(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public void encode(ByteBuf buffer){

    }

    @Override
    public void decode(ByteBuf buffer){
        this.playerId = PacketUtils.readString(buffer);
    }

    @Override
    public void processPacket(){
        Player player = NailedAPI.getPlayerRegistry().getPlayerById(this.playerId);
        if(player != null){
            NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.DisplayLogin(), player.getEntity());
        }
    }
}

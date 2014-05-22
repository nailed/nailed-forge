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
public class PacketCheckAccount extends IpcPacket {

    public String playerId;
    public String data;
    public int type;

    public PacketCheckAccount() {

    }

    public PacketCheckAccount(String playerId, String data, int type) {
        this.playerId = playerId;
        this.data = data;
        this.type = type;
    }

    @Override
    public void encode(ByteBuf buffer) {
        PacketUtils.writeString(this.playerId, buffer);
        PacketUtils.writeString(this.data, buffer);
        buffer.writeByte(this.type);
    }

    @Override
    public void decode(ByteBuf buffer) {
        this.playerId = PacketUtils.readString(buffer);
        this.data = PacketUtils.readString(buffer);
        this.type = buffer.readByte();
    }

    @Override
    public void processPacket() {
        Player player = NailedAPI.getPlayerRegistry().getPlayerById(this.playerId);
        if(player == null){
            return;
        }
        NailedPacket.FieldStatus res = new NailedPacket.FieldStatus();
        res.field = this.type;
        if(this.data.equals("used")){
            res.status = 'c';
            res.content = "In use";
        }else{
            res.status = 'a';
            res.content = "OK!";
        }
        NailedNetworkHandler.sendPacketToPlayer(res, player.getEntity());
    }
}

package jk_5.nailed.ipc.packet;

import io.netty.buffer.ByteBuf;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.ipc.PacketUtils;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketCheckAccount extends IpcPacket {

    public String playerId;
    public String data;
    public int type;

    @Override
    public void encode(ByteBuf buffer){
        PacketUtils.writeString(this.playerId, buffer);
        PacketUtils.writeString(this.data, buffer);
        buffer.writeByte(this.type);
    }

    @Override
    public void decode(ByteBuf buffer){
        this.playerId = PacketUtils.readString(buffer);
        this.data = PacketUtils.readString(buffer);
        this.type = buffer.readByte();
    }

    @Override
    public void processPacket(){
        Player player = NailedAPI.getPlayerRegistry().getPlayerById(this.playerId);
        if(player == null) return;
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

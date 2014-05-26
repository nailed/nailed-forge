package jk_5.nailed.ipc.packet;

import io.netty.buffer.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.ipc.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketChat extends IpcPacket {

    public String targetPlayerId;
    public String message;

    @Override
    public void encode(ByteBuf buffer) {

    }

    @Override
    public void decode(ByteBuf buffer) {
        this.targetPlayerId = PacketUtils.readString(buffer);
        this.message = PacketUtils.readString(buffer);
    }

    @Override
    public void processPacket() {
        Player p = NailedAPI.getPlayerRegistry().getPlayerById(this.targetPlayerId);
        if(p == null) return;
        p.sendChat(this.message);
    }
}

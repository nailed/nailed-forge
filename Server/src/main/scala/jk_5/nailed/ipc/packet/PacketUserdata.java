package jk_5.nailed.ipc.packet;

import io.netty.buffer.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.chat.joinmessage.*;
import jk_5.nailed.ipc.*;
import jk_5.nailed.web.auth.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketUserdata extends IpcPacket {

    private String id;
    private String username;
    private String fullname;
    private String email;

    public PacketUserdata() {

    }

    public PacketUserdata(String id, String username, String fullname, String email) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.email = email;
    }

    @Override
    public void encode(ByteBuf buffer) {

    }

    @Override
    public void decode(ByteBuf buffer) {
        this.id = PacketUtils.readString(buffer);
        this.username = PacketUtils.readString(buffer);
        this.fullname = PacketUtils.readString(buffer);
        this.email = PacketUtils.readString(buffer);
    }

    @Override
    public void processPacket() {
        Player p = NailedAPI.getPlayerRegistry().getPlayerByUsername(this.username);
        if(p == null){
            return;
        }
        p.setWebUser(new WebUser(id, username, fullname, email, true));
        JoinMessageSender.onPlayerJoin(p);
    }
}

package jk_5.nailed.ipc.packet;

import com.mojang.authlib.*;

import io.netty.buffer.*;

import jk_5.nailed.ipc.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketPlayerJoin extends IpcPacket {

    private GameProfile profile;
    private String ip;

    public PacketPlayerJoin() {

    }

    public PacketPlayerJoin(GameProfile profile, String ip) {
        this.profile = profile;
        this.ip = ip;
    }

    @Override
    public void encode(ByteBuf buffer) {
        PacketUtils.writeString(this.profile.getId(), buffer);
        PacketUtils.writeString(this.profile.getName(), buffer);
        PacketUtils.writeString(this.ip, buffer);
    }

    @Override
    public void decode(ByteBuf buffer) {

    }

    @Override
    public void processPacket() {

    }
}

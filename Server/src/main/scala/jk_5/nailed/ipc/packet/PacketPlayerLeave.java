package jk_5.nailed.ipc.packet;

import com.mojang.authlib.*;

import io.netty.buffer.*;

import jk_5.nailed.ipc.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketPlayerLeave extends IpcPacket {

    private GameProfile profile;

    public PacketPlayerLeave() {

    }

    public PacketPlayerLeave(GameProfile profile) {
        this.profile = profile;
    }

    @Override
    public void encode(ByteBuf buffer) {
        PacketUtils.writeString(this.profile.getId(), buffer);
    }

    @Override
    public void decode(ByteBuf buffer) {

    }

    @Override
    public void processPacket() {

    }
}

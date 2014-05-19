package jk_5.nailed.ipc.packet;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.ipc.PacketUtils;

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
    public void encode(ByteBuf buffer){
        PacketUtils.writeString(this.profile.getId(), buffer);
    }

    @Override
    public void decode(ByteBuf buffer){

    }

    @Override
    public void processPacket() {

    }
}

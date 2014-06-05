package jk_5.nailed.ipc.packet;

import java.util.Set;

import com.google.common.collect.Sets;

import io.netty.buffer.ByteBuf;

import jk_5.nailed.ipc.PacketUtils;
import jk_5.nailed.ipc.mappack.IpcMappackRegistry;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketListMappacks extends IpcPacket {

    private Set<String> mappacks;

    @Override
    public void encode(ByteBuf buffer) {

    }

    @Override
    public void decode(ByteBuf buffer) {
        int length = PacketUtils.readVarInt(buffer);
        this.mappacks = Sets.newHashSet();
        for(int i = 0; i < length; i++){
            mappacks.add(PacketUtils.readString(buffer));
        }
    }

    @Override
    public void processPacket() {
        IpcMappackRegistry.setRemoteMappacks(this.mappacks);
    }
}

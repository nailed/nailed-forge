package jk_5.nailed.network.packets;

import java.util.*;

import net.minecraft.network.*;
import net.minecraft.world.chunk.*;

/**
 * Created by matthias on 13-5-14.
 */
public class CustomBulkChunkPacket extends Packet {

    private List<Chunk> chunks;

    public CustomBulkChunkPacket() {
    }

    public CustomBulkChunkPacket(List<Chunk> chunks) {
        this.chunks = chunks;
    }

    public void readPacketData(PacketBuffer buffer) {
    }

    public void processPacket(INetHandler handler) {
    }

    public void writePacketData(PacketBuffer buffer) {
    }

    public List<Chunk> getChunks() {
        return this.chunks;
    }
}

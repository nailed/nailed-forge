package jk_5.nailed.network.packets;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;

import java.io.IOException;
import java.util.List;

/**
 * Created by matthias on 13-5-14.
 */
public class CustomBulkChunkPacket extends Packet {
    private List<Chunk> chunks;

    public CustomBulkChunkPacket(){}

    public CustomBulkChunkPacket(List<Chunk> chunks){
        this.chunks = chunks;
    }

    public void readPacketData(PacketBuffer buffer) throws IOException {
        return;
    }

    public void processPacket(INetHandler handler)
    {
        return;
    }

    public void writePacketData(PacketBuffer buffer){
        return;
    }

    public List<Chunk> getChunks(){
        return this.chunks;
    }
}

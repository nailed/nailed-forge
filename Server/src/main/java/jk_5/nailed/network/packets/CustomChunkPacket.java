package jk_5.nailed.network.packets;

import net.minecraft.network.*;
import net.minecraft.world.chunk.*;

/**
 * Created by matthias on 13-5-14.
 */
public class CustomChunkPacket extends Packet {

    public Chunk chunk;
    public boolean groundUpCont;
    public int i;

    public CustomChunkPacket() {
    }

    public CustomChunkPacket(Chunk chunk, boolean groundUpCont, int i) {
        this.chunk = chunk;
        this.groundUpCont = groundUpCont;
        this.i = i;
    }

    public void readPacketData(PacketBuffer buffer) {

    }

    public void processPacket(INetHandler handler) {

    }

    public void writePacketData(PacketBuffer buffer) {

    }

    public Chunk getChunk() {
        return this.chunk;
    }
}

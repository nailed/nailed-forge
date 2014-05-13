package jk_5.nailed.network.packets;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by matthias on 13-5-14.
 */
public class CustomChunkPacket extends Packet{
    public Chunk chunk;
    public boolean groundUpCont;
    public int i;

    public CustomChunkPacket(){}

    public CustomChunkPacket(Chunk chunk, boolean groundUpCont, int i){
        this.chunk = chunk;
        this.groundUpCont = groundUpCont;
        this.i = i;
    }

    public void readPacketData(PacketBuffer buffer) throws IOException{
        return;
    }

    public void processPacket(INetHandler handler){
        return;
    }

    public void writePacketData(PacketBuffer buffer) throws IOException{
        return;
    }

    public Chunk getChunk(){
        return this.chunk;
    }
}

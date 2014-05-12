package jk_5.nailed.network;

import jk_5.nailed.NailedLog;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.world.chunk.Chunk;

/**
 * No description given
 *
 * @author jk-5
 */
public class ChunkPacketAdapter {

    public static S21PacketChunkData.Extracted adaptChunk(Chunk chunk, boolean bool, int i){
        NailedLog.info("Serializing chunk " + chunk.toString());

        return null;
    }
}

package jk_5.nailed.network;

import jk_5.nailed.NailedLog;
import jk_5.nailed.blocks.NailedBlock;
import net.minecraft.block.Block;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

/**
 * No description given
 *
 * @author jk-5
 */
public class ChunkPacketAdapter {

    public static S21PacketChunkData.Extracted adaptChunk(Chunk chunk, boolean bool, int i){
        NailedLog.info("Serializing chunk " + chunk.toString());
        int j = 0;
        ExtendedBlockStorage[] aExtendedBlockStorage = chunk.getBlockStorageArray();
        ExtendedBlockStorage[] aextendedblockstorage = new ExtendedBlockStorage[aExtendedBlockStorage.length];

        int k = 0;
        S21PacketChunkData.Extracted extracted = new S21PacketChunkData.Extracted();
        byte[] abyte = new byte[196864];

        int l;

        for( l = 0; l < aExtendedBlockStorage.length; ++l){
            ExtendedBlockStorage array = aExtendedBlockStorage[l];
            if(array == null) continue;
            ExtendedBlockStorage pExtendedBlockStorage = new ExtendedBlockStorage(array.getYLocation(), true);

            pExtendedBlockStorage.setBlockLSBArray(array.getBlockLSBArray());
            pExtendedBlockStorage.setBlocklightArray(array.getBlocklightArray());
            pExtendedBlockStorage.setBlockMetadataArray(array.getMetadataArray());
            pExtendedBlockStorage.setBlockMSBArray(array.getBlockMSBArray());
            pExtendedBlockStorage.setSkylightArray(array.getSkylightArray());
            aextendedblockstorage[l] = pExtendedBlockStorage;
        }

        for( ExtendedBlockStorage extendedBlockStorage : aextendedblockstorage){
            if (extendedBlockStorage != null){
                for (int x = 0; x < 16; ++x){
                    for (int y = 0; y < 16; ++y){
                        for (int z = 0; z < 16; ++z){
                            Block block = extendedBlockStorage.getBlockByExtId(x, y, z);
                            if (block instanceof NailedBlock){
                                extendedBlockStorage.func_150818_a(x, y, z, ((NailedBlock) block).getReplacementBlock());
                                extendedBlockStorage.setExtBlockMetadata(x, y, z, ((NailedBlock) block).getReplacementMetadata());
                            }
                        }
                    }
                }
            }
        }

        if (bool)
        {
            chunk.sendUpdates = true;
        }

        for (l = 0; l < aextendedblockstorage.length; ++l)
        {
            if (aextendedblockstorage[l] != null && (!bool || !aextendedblockstorage[l].isEmpty()) && (i & 1 << l) != 0)
            {
                extracted.field_150280_b |= 1 << l;

                if (aextendedblockstorage[l].getBlockMSBArray() != null)
                {
                    extracted.field_150281_c |= 1 << l;
                    ++k;
                }
            }
        }

        for (l = 0; l < aextendedblockstorage.length; ++l)
        {
            if (aextendedblockstorage[l] != null && (!bool || !aextendedblockstorage[l].isEmpty()) && (i & 1 << l) != 0)
            {
                byte[] abyte1 = aextendedblockstorage[l].getBlockLSBArray();
                System.arraycopy(abyte1, 0, abyte, j, abyte1.length);
                j += abyte1.length;
            }
        }

        NibbleArray nibblearray;

        for (l = 0; l < aextendedblockstorage.length; ++l)
        {
            if (aextendedblockstorage[l] != null && (!bool || !aextendedblockstorage[l].isEmpty()) && (i & 1 << l) != 0)
            {
                nibblearray = aextendedblockstorage[l].getMetadataArray();
                System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                j += nibblearray.data.length;
            }
        }

        for (l = 0; l < aextendedblockstorage.length; ++l)
        {
            if (aextendedblockstorage[l] != null && (!bool || !aextendedblockstorage[l].isEmpty()) && (i & 1 << l) != 0)
            {
                nibblearray = aextendedblockstorage[l].getBlocklightArray();
                System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                j += nibblearray.data.length;
            }
        }

        if (!chunk.worldObj.provider.hasNoSky)
        {
            for (l = 0; l < aextendedblockstorage.length; ++l)
            {
                if (aextendedblockstorage[l] != null && (!bool || !aextendedblockstorage[l].isEmpty()) && (i & 1 << l) != 0)
                {
                    nibblearray = aextendedblockstorage[l].getSkylightArray();
                    System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                    j += nibblearray.data.length;
                }
            }
        }

        if (k > 0)
        {
            for (l = 0; l < aextendedblockstorage.length; ++l)
            {
                if (aextendedblockstorage[l] != null && (!bool || !aextendedblockstorage[l].isEmpty()) && aextendedblockstorage[l].getBlockMSBArray() != null && (i & 1 << l) != 0)
                {
                    nibblearray = aextendedblockstorage[l].getBlockMSBArray();
                    System.arraycopy(nibblearray.data, 0, abyte, j, nibblearray.data.length);
                    j += nibblearray.data.length;
                }
            }
        }

        if (bool)
        {
            byte[] abyte2 = chunk.getBiomeArray();
            System.arraycopy(abyte2, 0, abyte, j, abyte2.length);
            j += abyte2.length;
        }

        extracted.field_150282_a = new byte[j];
        System.arraycopy(abyte, 0, extracted.field_150282_a, 0, j);
        return extracted;
    }
}

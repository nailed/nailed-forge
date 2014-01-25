package jk_5.nailed.map.gen;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.MappackContainingWorldProvider;
import jk_5.nailed.map.mappack.Mappack;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedWorldProvider extends WorldProvider implements MappackContainingWorldProvider {

    private int mapID;
    private Map map;

    @Override
    public void setDimension(int dim){
        this.mapID = dim;
        super.setDimension(dim);
    }

    @Override
    protected void registerWorldChunkManager(){
        this.map = MapLoader.instance().getMap(this.dimensionId);
        this.worldChunkMgr = new VoidWorldChunkManager(this.worldObj);
    }

    public void writeData(ByteBuf buffer){

    }

    @Override
    public IChunkProvider createChunkGenerator(){
        return new VoidChunkProvider(this.worldObj);
    }

    @Override
    public String getDimensionName(){
        return "Nailed " + (this.hasMappack() ? this.getMappack().getMappackMetadata().getName() : "") + " " + this.mapID;
    }

    @Override
    public String getSaveFolder(){
        return null;
    }

    @Override
    public boolean hasMappack(){
        return this.map.getMappack() != null;
    }

    @Override
    public Mappack getMappack(){
        return this.map.getMappack();
    }

    @Override
    public ChunkCoordinates getRandomizedSpawnPoint(){
        if(this.hasMappack()){
            return new ChunkCoordinates(this.map.getMappack().getMappackMetadata().getSpawnPoint());
        }else{
            return super.getRandomizedSpawnPoint();
        }
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player){
        return player.dimension;
    }

    public NailedPacket.MapData getMapDataPacket(){
        ByteBuf buffer = Unpooled.buffer();
        this.writeData(buffer);
        return new NailedPacket.MapData(this.mapID, buffer);
    }

    public void broadcastMapData(){
        NailedNetworkHandler.sendPacketToAllPlayersInDimension(this.getMapDataPacket(), this.mapID);
    }

    public void sendMapData(EntityPlayer player){
        NailedNetworkHandler.sendPacketToPlayer(this.getMapDataPacket(), player);
    }
}

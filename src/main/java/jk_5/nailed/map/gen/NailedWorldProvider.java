package jk_5.nailed.map.gen;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.Mappack;
import jk_5.nailed.map.MappackContainingWorldProvider;
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

    @Override
    public IChunkProvider createChunkGenerator(){
        return new VoidChunkProvider(this.worldObj);
    }

    @Override
    public String getDimensionName(){
        return "Nailed " + (this.hasMappack() ? this.getMappack().getName() : "") + " " + this.mapID;
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
            return new ChunkCoordinates(this.map.getMappack().getMappackConfig().spawnPoint);
        }else{
            return super.getRandomizedSpawnPoint();
        }
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player){
        return player.dimension;
    }
}

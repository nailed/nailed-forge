package jk_5.nailed.map.gen;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedWorldProvider extends WorldProvider {

    private int mapID;
    private Map map;

    @Override
    public void setDimension(int dim) {
        this.mapID = dim;
        super.setDimension(dim);
    }

    @Override
    protected void registerWorldChunkManager() {
        this.map = NailedAPI.getMapLoader().getMap(this.dimensionId);
        this.worldChunkMgr = new VoidWorldChunkManager(this.worldObj);
    }

    @Override
    public void calculateInitialWeather() {
        this.updateWeather();
    }

    @Override
    public void updateWeather() {
        super.updateWeather();

        this.worldObj.rainingStrength = 0;
        this.worldObj.prevRainingStrength = 0;
        this.worldObj.thunderingStrength = 0;
        this.worldObj.prevThunderingStrength = 0;
    }

    @Override
    public boolean canDoLightning(Chunk chunk) {
        return false;
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        return new VoidChunkProvider(this.worldObj);
    }

    @Override
    public String getDimensionName() {
        return "Nailed " + (this.map.getMappack() != null ? this.map.getMappack().getMappackMetadata().getName() : "") + " " + this.mapID;
    }

    @Override
    public String getSaveFolder() {
        return "../" + this.map.getSaveFileName();
    }

    @Override
    public ChunkCoordinates getRandomizedSpawnPoint() {
        if(this.map.getMappack() != null){
            return this.map.getMappack().getMappackMetadata().getSpawnPoint().toChunkCoordinates();
        }else{
            return new ChunkCoordinates(0, 64, 0);
        }
    }

    @Override
    public ChunkCoordinates getSpawnPoint() {
        return this.getRandomizedSpawnPoint();
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player) {
        return player.dimension;
    }
}

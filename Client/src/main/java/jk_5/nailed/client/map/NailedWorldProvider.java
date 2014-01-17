package jk_5.nailed.client.map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedWorldProvider extends WorldProvider {

    private int mapID;

    @Override
    public void setDimension(int dim){
        this.mapID = dim;
        super.setDimension(dim);
    }

    @Override
    protected void registerWorldChunkManager(){
        this.worldChunkMgr = new WorldChunkManager(this.worldObj);
    }

    @Override
    public IChunkProvider createChunkGenerator(){
        return new VoidChunkProvider(this.worldObj);
    }

    @Override
    public String getDimensionName(){
        return "Nailed " + this.mapID;
    }

    @Override
    public String getSaveFolder(){
        return null;
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player){
        return player.dimension;
    }
}

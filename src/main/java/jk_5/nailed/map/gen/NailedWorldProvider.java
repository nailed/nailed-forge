package jk_5.nailed.map.gen;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedWorldProvider extends WorldProvider {

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
        return "Nailed";
    }

    @Override
    public String getSaveFolder(){
        return "maps/map" + this.mapID + ""; //TODO: mappack name
    }
}

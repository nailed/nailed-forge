package jk_5.nailed.map;

import jk_5.nailed.map.mappack.Mappack;
import net.minecraftforge.common.DimensionManager;

import java.io.File;

/**
 * No description given
 *
 * @author jk-5
 */
public final class PotentialMap {

    private final int id;
    private final Mappack pack;

    public PotentialMap(Mappack mappack){
        this.id = DimensionManager.getNextFreeDimId();
        this.pack = mappack;
    }

    public String getSaveFileName(){
        return "map" + (this.pack == null ? "" : "_" + this.pack.getMappackID()) + "_" + this.id;
    }

    public File getSaveFolder(){
        return new File(MapLoader.getMapsFolder(), this.getSaveFileName());
    }

    public Map createMap(){
        return new Map(this.pack, this.id);
    }

    public static String getSaveFileName(Map map){
        return "map" + (map.getMappack() == null ? "" : "_" + map.getMappack().getMappackID()) + "_" + map.getID();
    }
}

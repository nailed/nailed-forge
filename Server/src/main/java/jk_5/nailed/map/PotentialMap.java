package jk_5.nailed.map;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.MapBuilder;
import jk_5.nailed.api.map.Mappack;

import java.io.File;

/**
 * No description given
 *
 * @author jk-5
 */
public final class PotentialMap implements MapBuilder {

    private final int ID;
    private final Mappack mappack;

    public PotentialMap(Mappack mappack){
        this.ID = NailedMapLoader.instance().reserveDimensionId();
        this.mappack = mappack;
    }

    @Override
    public String getSaveFileName(){
        return "map" + (this.mappack == null ? "" : "_" + this.mappack.getMappackID()) + "_" + this.ID;
    }

    @Override
    public File getSaveFolder(){
        return new File(NailedAPI.getMapLoader().getMapsFolder(), this.getSaveFileName());
    }

    @Override
    public Map build(){
        return new NailedMap(this.mappack, this.ID);
    }

    public static String getSaveFileName(Map map){
        return "map" + (map.getMappack() == null ? "" : "_" + map.getMappack().getMappackID()) + "_" + map.getID();
    }

    @Override
    public int getID() {
        return this.ID;
    }

    @Override
    public Mappack getMappack() {
        return this.mappack;
    }
}

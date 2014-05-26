package jk_5.nailed.map;

import java.io.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public final class PotentialMap implements MapBuilder {

    private final int id;
    private final Mappack mappack;

    public PotentialMap(Mappack mappack) {
        this.id = NailedMapLoader.instance().reserveDimensionId();
        this.mappack = mappack;
    }

    @Override
    public String getSaveFileName() {
        return "map" + (this.mappack == null ? "" : "_" + this.mappack.getMappackID()) + "_" + this.id;
    }

    @Override
    public File getSaveFolder() {
        return new File(NailedAPI.getMapLoader().getMapsFolder(), this.getSaveFileName());
    }

    @Override
    public Map build() {
        return new NailedMap(this.mappack, this.id);
    }

    public static String getSaveFileName(Map map) {
        return "map" + (map.getMappack() == null ? "" : "_" + map.getMappack().getMappackID()) + "_" + map.getID();
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public Mappack getMappack() {
        return this.mappack;
    }
}

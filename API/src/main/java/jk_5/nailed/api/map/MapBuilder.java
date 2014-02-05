package jk_5.nailed.api.map;

import java.io.File;

/**
 * No description given
 *
 * @author jk-5
 */
public interface MapBuilder {

    public int getID();
    public Mappack getMappack();
    public String getSaveFileName();
    public File getSaveFolder();
    public Map build();
}

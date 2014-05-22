package jk_5.nailed.api.map;

import java.io.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface MapBuilder {

    int getID();
    Mappack getMappack();
    String getSaveFileName();
    File getSaveFolder();
    Map build();
}

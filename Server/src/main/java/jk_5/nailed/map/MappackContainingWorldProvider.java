package jk_5.nailed.map;

import jk_5.nailed.map.mappack.Mappack;

/**
 * No description given
 *
 * @author jk-5
 */
public interface MappackContainingWorldProvider {

    boolean hasMappack();
    Mappack getMappack();
}
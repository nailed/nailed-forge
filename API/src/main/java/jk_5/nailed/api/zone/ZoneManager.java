package jk_5.nailed.api.zone;

import java.util.*;

import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ZoneManager {

    Set<IZone> getZones(double x, double y, double z);
    Set<IZone> getZones(Player player);
}

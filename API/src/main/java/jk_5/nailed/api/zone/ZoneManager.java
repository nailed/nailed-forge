package jk_5.nailed.api.zone;

import jk_5.nailed.api.player.Player;

import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ZoneManager {

    public Set<IZone> getZones(double x, double y, double z);
    public Set<IZone> getZones(Player player);
}

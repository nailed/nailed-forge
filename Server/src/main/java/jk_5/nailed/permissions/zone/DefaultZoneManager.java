package jk_5.nailed.permissions.zone;

import com.google.common.collect.ImmutableSet;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.zone.IZone;
import jk_5.nailed.api.zone.ZoneManager;

import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class DefaultZoneManager implements ZoneManager {

    private final Map map;

    public DefaultZoneManager(Map map){
        this.map = map;
    }

    @Override
    public Set<IZone> getZones(double x, double y, double z) {
        return ImmutableSet.of(); //TODO: jk-5: implement this
    }
}

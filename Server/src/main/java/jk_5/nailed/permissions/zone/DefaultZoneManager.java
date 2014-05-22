package jk_5.nailed.permissions.zone;

import java.util.*;

import com.google.common.base.*;
import com.google.common.collect.*;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.*;
import jk_5.nailed.api.zone.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class DefaultZoneManager implements ZoneManager {

    private final ZoneConfig zones;

    public DefaultZoneManager(Map map) {
        if(map.getMappack() != null){
            Preconditions.checkNotNull(map.getMappack().getZoneConfig(), "ZoneConfig may not be null!");
            this.zones = map.getMappack().getZoneConfig().reMake();
        }else{
            this.zones = new DefaultZoneConfig();
        }
    }

    @Override
    public Set<IZone> getZones(double x, double y, double z) {
        Set<IZone> ret = Sets.newHashSet();
        for(IZone zone : this.zones.getZones()){
            if(zone.isInZone(x, y, z)){
                ret.add(zone);
            }
        }
        return ret;
    }

    @Override
    public Set<IZone> getZones(Player player) {
        return this.getZones(player.getEntity().posX, player.getEntity().posY, player.getEntity().posZ);
    }
}

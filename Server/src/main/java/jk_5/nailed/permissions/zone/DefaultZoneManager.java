package jk_5.nailed.permissions.zone;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.zone.IZone;
import jk_5.nailed.api.zone.ZoneConfig;
import jk_5.nailed.api.zone.ZoneManager;

import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class DefaultZoneManager implements ZoneManager {

    private final ZoneConfig zones;

    public DefaultZoneManager(Map map){
        if(map.getMappack() != null){
            Preconditions.checkNotNull(map.getMappack().getZoneConfig(), "ZoneConfig may not be null!");
            this.zones = map.getMappack().getZoneConfig().clone();
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

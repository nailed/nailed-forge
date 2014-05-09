package jk_5.nailed.permissions.zone;

import com.google.common.collect.Maps;
import jk_5.nailed.api.events.RegisterZoneEvent;
import jk_5.nailed.api.zone.IZoneType;
import jk_5.nailed.api.zone.ZoneRegistry;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedZoneRegistry implements ZoneRegistry {

    private boolean locked = false;
    private Map<String, IZoneType> zoneTypes = Maps.newHashMap();

    @Override
    public void lockZones() {
        this.locked = true;
    }

    @Override
    public void registerZones() {
        MinecraftForge.EVENT_BUS.post(new RegisterZoneEvent(this.zoneTypes));
    }
}

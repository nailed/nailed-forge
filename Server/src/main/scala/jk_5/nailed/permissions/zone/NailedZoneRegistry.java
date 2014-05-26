package jk_5.nailed.permissions.zone;

import java.util.*;
import javax.annotation.*;

import com.google.common.collect.*;

import net.minecraftforge.common.*;

import jk_5.nailed.api.events.*;
import jk_5.nailed.api.zone.*;

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

    @Nullable
    @Override
    public IZoneType getZoneType(@Nonnull String type) {
        return this.zoneTypes.get(type);
    }
}

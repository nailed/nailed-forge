package jk_5.nailed.api.events;

import java.util.*;

import cpw.mods.fml.common.eventhandler.*;

import jk_5.nailed.api.zone.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class RegisterZoneEvent extends Event {

    private final Map<String, IZoneType> zones;

    public RegisterZoneEvent(Map<String, IZoneType> zones) {
        this.zones = zones;
    }

    public void registerZoneType(String name, IZoneType type) {
        if(zones.containsKey(name)){
            throw new IllegalArgumentException("Zone type " + name + " already exists");
        }
        this.zones.put(name, type);
    }
}

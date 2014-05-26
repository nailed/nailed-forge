package jk_5.nailed.permissions.zone;

import java.util.*;

import com.google.common.collect.*;
import com.google.gson.*;

import jk_5.nailed.*;
import jk_5.nailed.api.*;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.zone.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class DefaultZoneConfig implements ZoneConfig {

    private final List<IZone> zones = Lists.newArrayList();

    public DefaultZoneConfig() {
    }

    public DefaultZoneConfig(JsonArray data) {
        for(JsonElement element : data){
            JsonObject obj = element.getAsJsonObject();
            if(obj.has("type")){
                IZoneType type = NailedAPI.getZoneRegistry().getZoneType(obj.get("type").getAsString());
                if(type == null){
                    NailedLog.warn("Unknown zone type {}", obj.get("type").getAsString());
                    continue;
                }
                try{
                    IZone zone = type.read(obj);
                    this.zones.add(zone);
                }catch(ZoneDataException e){
                    NailedLog.warn("Invalid or corrupt zone data", e);
                }
            }
        }
    }

    public ZoneConfig reMake() {
        DefaultZoneConfig config = new DefaultZoneConfig();
        for(IZone zone : this.zones){
            config.zones.add(zone.reMake());
        }
        return config;
    }

    @Override
    public List<IZone> getZones() {
        return this.zones;
    }
}

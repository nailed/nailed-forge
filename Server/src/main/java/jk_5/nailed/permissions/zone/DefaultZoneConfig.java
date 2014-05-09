package jk_5.nailed.permissions.zone;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.zone.IZone;
import jk_5.nailed.api.zone.IZoneType;
import jk_5.nailed.api.zone.ZoneConfig;
import jk_5.nailed.api.zone.ZoneDataException;

import java.util.List;

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

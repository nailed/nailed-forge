package jk_5.nailed.map;

import java.util.*;
import javax.annotation.*;

import com.google.common.collect.*;

import jk_5.nailed.api.map.Map;

/**
 * Created by matthias on 22-5-14.
 */
public class LocationHandler implements jk_5.nailed.api.map.LocationHandler {

    private HashMap<String, Location> locations = Maps.newHashMap();

    public LocationHandler(Map map) {

        if(map.getMappack() == null){
            return;
        }

        this.locations.putAll(map.getMappack().getMappackMetadata().getLocations());
    }

    public void addLocation(String name, Location location) {
        if(!locations.containsKey(name) && !(location == null)){
            locations.put(name, location);
        }
    }

    public void addLocations(HashMap<String, Location> map) {
        locations.putAll(map);
    }

    public void removeLocation(String name) {
        if(locations.containsKey(name)){
            locations.remove(name);
        }
    }

    public void removeLocations(List<String> list) {
        for(String string : list){
            if(locations.containsKey(string)){
                locations.remove(string);
            }
        }
    }

    public HashMap<String, Location> getLocations() {
        return this.locations;
    }

    @Nullable
    public Location getLocation(String name) {
        if(this.locations.containsKey(name)){
            return this.locations.get(name);
        }
        return null;
    }
}

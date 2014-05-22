package jk_5.nailed.api.map;

import jk_5.nailed.map.Location;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by matthias on 22-5-14.
 */
public interface LocationHandler {
    public void addLocation(String string, Location location);
    public void addLocations(HashMap<String, Location> map);
    public void removeLocation(String string);
    public void removeLocations(List<String> list);
    public HashMap<String, Location> getLocations();
    @Nullable
    public Location getLocation(String string);
}

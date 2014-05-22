package jk_5.nailed.api.map;

import java.util.*;
import javax.annotation.*;

import jk_5.nailed.map.*;

/**
 * Created by matthias on 22-5-14.
 */
public interface LocationHandler {

    void addLocation(String string, Location location);
    void addLocations(HashMap<String, Location> map);
    void removeLocation(String string);
    void removeLocations(List<String> list);
    HashMap<String, Location> getLocations();
    @Nullable
    Location getLocation(String string);
}

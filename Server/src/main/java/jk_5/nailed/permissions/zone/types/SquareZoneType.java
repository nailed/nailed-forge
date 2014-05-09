package jk_5.nailed.permissions.zone.types;

import com.google.gson.JsonObject;
import jk_5.nailed.api.zone.IZone;
import jk_5.nailed.api.zone.IZoneType;

import javax.annotation.Nullable;

/**
 * Created by matthias on 9-5-14.
 */
public class SquareZoneType implements IZoneType {
    @Nullable
    public IZone read(JsonObject object){
        String name = object.has("name") ? object.get("name").getAsString() : "";
        int x1 = object.has("x1") ? object.get("x1").getAsInt() : 0;
        int z1 = object.has("z1") ? object.get("z1").getAsInt() : 0;
        int x2 = object.has("x2") ? object.get("x2").getAsInt() : 1;
        int z2 = object.has("z2") ? object.get("z2").getAsInt() : 1;
        boolean inverted = object.has("inverted") ? object.get("inverted").getAsBoolean() : false;

        if(x2 <= x1 || z2 <= z1 || name.equals("")) return null;
        return new SquareZone(name, x1, z1, x2, z2, inverted);
    }
}

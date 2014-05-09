package jk_5.nailed.permissions.zone.types;

import com.google.gson.JsonObject;
import jk_5.nailed.api.zone.IZone;
import jk_5.nailed.api.zone.IZoneType;

import javax.annotation.Nullable;

/**
 * Created by matthias on 9-5-14.
 */
public class CircleZoneType implements IZoneType {

    @Override
    @Nullable
    public IZone read(JsonObject object){
        String name = object.has("name") ? object.get("name").getAsString() : "";
        int x = object.has("x") ? object.get("x").getAsInt() : 0;
        int y = object.has("y") ? object.get("y").getAsInt() : 0;
        int z = object.has("z") ? object.get("z").getAsInt() : 0;
        int r = object.has("r") ? object.get("r").getAsInt() : 1;
        boolean inverted = object.has("inverted") && object.get("inverted").getAsBoolean();

        if(r <= 0 || name.equals("")) return null;
        return new SphereZone(name, x, y, z, r, inverted);
    }
}

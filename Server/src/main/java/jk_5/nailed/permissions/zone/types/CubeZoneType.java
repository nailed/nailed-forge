package jk_5.nailed.permissions.zone.types;

import com.google.gson.JsonObject;
import jk_5.nailed.api.zone.IZone;
import jk_5.nailed.api.zone.IZoneType;

import javax.annotation.Nullable;

/**
 * Created by matthias on 9-5-14.
 */
public class CubeZoneType implements IZoneType {
    @Nullable
    public IZone read(JsonObject object){
        int x1 = object.has("x1") ? object.get("x1").getAsInt() : 0;
        int y1 = object.has("y1") ? object.get("y1").getAsInt() : 0;
        int z1 = object.has("z1") ? object.get("z1").getAsInt() : 0;
        int x2 = object.has("x2") ? object.get("x2").getAsInt() : 1;
        int y2 = object.has("y2") ? object.get("y2").getAsInt() : 1;
        int z2 = object.has("z2") ? object.get("z2").getAsInt() : 1;

        if(x2 <= x1 || y2 <= y1 || z2 <= z1) return null;
        return new CubeZone(x1, y1, z1, x2, y2, z2);
    }
}

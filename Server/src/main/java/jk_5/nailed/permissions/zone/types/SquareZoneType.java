package jk_5.nailed.permissions.zone.types;

import com.google.gson.JsonObject;
import jk_5.nailed.api.zone.IZone;
import jk_5.nailed.api.zone.IZoneType;
import jk_5.nailed.api.zone.ZoneDataException;

import javax.annotation.Nonnull;

/**
 * Created by matthias on 9-5-14.
 */
public class SquareZoneType implements IZoneType {

    @Override
    @Nonnull
    public IZone read(JsonObject object) throws ZoneDataException {
        if(!object.has("name") || object.get("name").getAsString().isEmpty()){
            throw new ZoneDataException("Zone does not have a name");
        }
        String name = object.has("name") ? object.get("name").getAsString() : "";
        if(object.has("x1") & object.has("z1") & object.has("x2") & object.has("z2")) {
            int x1 = object.get("x1").getAsInt();
            int z1 = object.get("z1").getAsInt();
            int x2 = object.get("x2").getAsInt();
            int z2 = object.get("z2").getAsInt();
            boolean inverted = object.has("inverted") && object.get("inverted").getAsBoolean();

            if (x2 <= x1 || z2 <= z1) {
                throw new ZoneDataException("Coordinate 1 should be smaller than coordinate 2");
            }
            return new SquareZone(name, x1, z1, x2, z2, inverted);
        } else {
            throw new ZoneDataException("Zone has to have all coords specified");
        }
    }
}

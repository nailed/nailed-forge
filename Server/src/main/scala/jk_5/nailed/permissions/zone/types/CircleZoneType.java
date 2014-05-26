package jk_5.nailed.permissions.zone.types;

import javax.annotation.*;

import com.google.gson.*;

import jk_5.nailed.api.zone.*;

/**
 * Created by matthias on 9-5-14.
 */
public class CircleZoneType implements IZoneType {

    @Override
    @Nonnull
    public IZone read(JsonObject object) throws ZoneDataException {
        if(!object.has("name") || object.get("name").getAsString().isEmpty()){
            throw new ZoneDataException("Zone does not have a name");
        }

        String name = object.get("name").getAsString();
        if(object.has("x") & object.has("y") & object.has("z") & object.has("r")){
            int x = object.get("x").getAsInt();
            int z = object.get("z").getAsInt();
            int r = object.get("r").getAsInt();

            boolean inverted = object.has("inverted") && object.get("inverted").getAsBoolean();

            if(r <= 0){
                throw new ZoneDataException("Zone has negative or zero size radius");
            }

            return new CircleZone(name, x, z, r, inverted);

        }else{
            throw new ZoneDataException("Zone has to have all coords specified");
        }
    }
}

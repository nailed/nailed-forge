package jk_5.nailed.api.zone;

import javax.annotation.*;

import com.google.gson.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IZoneType {

    @Nonnull
    IZone read(JsonObject json) throws ZoneDataException;
}

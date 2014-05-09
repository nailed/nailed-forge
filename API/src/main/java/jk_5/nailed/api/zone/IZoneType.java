package jk_5.nailed.api.zone;

import com.google.gson.JsonObject;

import javax.annotation.Nonnull;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IZoneType {
    @Nonnull IZone read(JsonObject json) throws ZoneDataException;
}

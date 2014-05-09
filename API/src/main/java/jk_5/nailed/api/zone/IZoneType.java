package jk_5.nailed.api.zone;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IZoneType {
    @Nullable
    IZone read(JsonObject json);
}

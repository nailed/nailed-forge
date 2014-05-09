package jk_5.nailed.api.zone;

import com.google.gson.JsonArray;

import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IZoneType {

    Set<NailedZone> read(JsonArray json);
}

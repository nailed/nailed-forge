package jk_5.nailed.map.stat.types;

import com.google.gson.*;

import jk_5.nailed.api.map.stat.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatTypeModifiable implements IStatType {

    @Override
    public void readAdditionalData(JsonObject obj, Stat stat) {
        stat.setDefaultState(obj.has("default") && obj.get("default").getAsBoolean());
    }
}

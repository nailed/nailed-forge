package jk_5.nailed.map.stat.types;

import com.google.gson.JsonObject;
import jk_5.nailed.api.map.stat.IStatType;
import jk_5.nailed.api.map.stat.Stat;

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

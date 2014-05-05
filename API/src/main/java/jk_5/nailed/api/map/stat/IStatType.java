package jk_5.nailed.api.map.stat;

import com.google.gson.JsonObject;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IStatType {

    void readAdditionalData(JsonObject obj, Stat stat);
}

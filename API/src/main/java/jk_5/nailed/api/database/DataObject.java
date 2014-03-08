package jk_5.nailed.api.database;

import com.google.gson.JsonObject;

/**
 * No description given
 *
 * @author jk-5
 */
public interface DataObject {

    public void read(JsonObject data);
    public void write(JsonObject data);
}

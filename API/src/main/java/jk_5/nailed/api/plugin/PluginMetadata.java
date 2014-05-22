package jk_5.nailed.api.plugin;

import com.google.gson.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PluginMetadata {

    private String name;
    private String mainClass;

    public String getName() {
        return name;
    }

    public String getMainClass() {
        return mainClass;
    }

    public static PluginMetadata read(JsonObject object) {
        PluginMetadata ret = new PluginMetadata();
        ret.name = object.get("name").getAsString();
        ret.mainClass = object.get("mainClass").getAsString();
        return ret;
    }
}

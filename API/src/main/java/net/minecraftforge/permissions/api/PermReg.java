package net.minecraftforge.permissions.api;

import com.google.gson.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PermReg {

    public final String key;
    public final RegisteredPermValue role;
    public final JsonObject data;

    /**
     * A PermReg entry
     *
     * @param key   A permission node
     * @param value Default value of your permission node, see {@link net.minecraftforge.permissions.api.RegisteredPermValue} for valid values
     * @param obj   Any additional json objects you need, can be null
     */
    public PermReg(String key, RegisteredPermValue value, JsonObject obj) {
        this.key = key;
        this.role = value;
        this.data = obj;
    }
}

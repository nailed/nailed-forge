package jk_5.nailed.permissions.zone;

import com.google.gson.JsonObject;
import jk_5.nailed.api.zone.NailedZone;

import javax.annotation.Nullable;

/**
 * Created by matthias on 9-5-14.
 *
 * Secure non-break zone
 */
public class NailedSecureZone implements NailedZone {
    private int x1;
    private int y1;
    private int z1;
    private int x2;
    private int y2;
    private int z2;
    private boolean isSecure = true;

    public NailedSecureZone(int x1, int y1, int z1, int x2, int y2, int z2, boolean isSecure){
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.isSecure = isSecure;
    }

    public boolean isInZone(int x, int y, int z){
        return (x1<x && x<x2 && y1 < y && y < y2 && z1 < z && z < z2);
    }

    public boolean isSecure(){ return this.isSecure; }

    @Nullable
    public static NailedSecureZone readFrom(JsonObject object){
        int x1 = object.has("x1") ? object.get("x1").getAsInt() : 0;
        int y1 = object.has("y1") ? object.get("y1").getAsInt() : 0;
        int z1 = object.has("z1") ? object.get("z1").getAsInt() : 0;
        int x2 = object.has("x2") ? object.get("x1").getAsInt() : 1;
        int y2 = object.has("y2") ? object.get("y2").getAsInt() : 1;
        int z2 = object.has("z2") ? object.get("z2").getAsInt() : 1;
        boolean isSecure = object.has("isSecure") ? object.get("isSecure").getAsBoolean() : false;

        if(x2 < x1 || y2 < y1 || z2 < z1) return null;
        return new NailedSecureZone(x1, y1, z1, x2, y2, z2, isSecure);
    }
}

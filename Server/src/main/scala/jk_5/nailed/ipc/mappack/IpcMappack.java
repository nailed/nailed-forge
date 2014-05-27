package jk_5.nailed.ipc.mappack;

import java.io.*;
import java.util.*;
import javax.annotation.*;

import com.google.gson.*;
import com.google.gson.reflect.*;

import jk_5.nailed.api.concurrent.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.stat.*;
import jk_5.nailed.api.scripting.*;
import jk_5.nailed.api.zone.*;
import jk_5.nailed.ipc.filestore.*;
import jk_5.nailed.map.mappack.*;
import jk_5.nailed.permissions.zone.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class IpcMappack implements Mappack {

    private static final Gson gson = new Gson();

    public final MappackFilestore filestore = new MappackFilestore();
    public final MappackFilestore luaFilestore = new MappackFilestore();

    private final String id;
    private final MappackMetadata metadata;
    private final StatConfig statConfig;
    private final ZoneConfig zoneConfig;

    public IpcMappack(JsonObject json) {
        this.id = json.get("mpid").getAsString();
        this.metadata = new JsonMappackMetadata(json);
        if(metadata.getName() == null){
            ((JsonMappackMetadata) metadata).name = this.id;
        }
        this.filestore.files = gson.fromJson(json.get("worldFiles"), new TypeToken<List<MappackFile>>() {
        }.getType());
        this.luaFilestore.files = gson.fromJson(json.get("luaFiles"), new TypeToken<List<MappackFile>>() {
        }.getType());
        this.filestore.refresh();
        this.luaFilestore.refresh();
        this.statConfig = new jk_5.nailed.map.stat.StatConfig(json.get("stats").getAsJsonArray());
        this.zoneConfig = new DefaultZoneConfig(json.get("zones").getAsJsonArray());
    }

    @Override
    @Nonnull
    public String getMappackID() {
        return this.id;
    }

    @Override
    @Nonnull
    public MappackMetadata getMappackMetadata() {
        return this.metadata;
    }

    @Override
    @Nonnull
    public StatConfig getStatConfig() {
        return this.statConfig;
    }

    @Override
    public void prepareWorld(@Nonnull final File destinationDir, @Nullable final Callback<Void> callback) {
        this.filestore.requestMissingFiles(new Callback<Void>() {
            @Override
            public void callback(Void obj) {
                filestore.reconstruct(destinationDir);
                if(callback != null){
                    callback.callback(null);
                }
            }
        });
        this.luaFilestore.requestMissingFiles(null);
    }

    @Override
    @Nonnull
    public Map createMap(@Nonnull MapBuilder mapBuilder) {
        return mapBuilder.build();
    }

    @Override
    public boolean saveAsMappack(@Nonnull Map map) {
        return false;
    }

    @Override
    @Nullable
    public IMount createMount() {
        return new FilestoreMount(this.luaFilestore);
    }

    @Nonnull
    @Override
    public ZoneConfig getZoneConfig() {
        return this.zoneConfig;
    }
}

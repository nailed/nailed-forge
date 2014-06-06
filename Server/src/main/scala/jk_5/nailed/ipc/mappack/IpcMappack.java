package jk_5.nailed.ipc.mappack;

import java.io.File;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.MapBuilder;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.map.stat.StatConfig;
import jk_5.nailed.api.scripting.IMount;
import jk_5.nailed.api.zone.ZoneConfig;
import jk_5.nailed.ipc.filestore.FilestoreMount;
import jk_5.nailed.ipc.filestore.MappackFile;
import jk_5.nailed.ipc.filestore.MappackFilestore;
import jk_5.nailed.map.mappack.JsonMappackMetadata;
import jk_5.nailed.map.stat.DefaultStatConfig;
import jk_5.nailed.permissions.zone.DefaultZoneConfig;

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
        this.filestore.files = gson.fromJson(json.get("worldFiles"), new TypeToken<List<MappackFile>>() {}.getType());
        this.luaFilestore.files = gson.fromJson(json.get("luaFiles"), new TypeToken<List<MappackFile>>() {}.getType());
        this.filestore.refresh();
        this.luaFilestore.refresh();
        if(json.has("stats")){
            this.statConfig = new DefaultStatConfig(json.get("stats").getAsJsonArray());
        }else{
            this.statConfig = new DefaultStatConfig();
        }
        if(json.has("zones")){
            this.zoneConfig = new DefaultZoneConfig(json.get("zones").getAsJsonArray());
        }else{
            this.zoneConfig = new DefaultZoneConfig();
        }
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

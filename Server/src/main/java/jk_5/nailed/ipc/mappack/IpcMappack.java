package jk_5.nailed.ipc.mappack;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.MapBuilder;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.map.stat.StatConfig;
import jk_5.nailed.api.scripting.IMount;
import jk_5.nailed.ipc.filestore.MappackFile;
import jk_5.nailed.ipc.filestore.MappackFilestore;
import jk_5.nailed.map.mappack.JsonMappackMetadata;

import java.io.File;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class IpcMappack implements Mappack {

    private static final Gson gson = new Gson();

    private final String id;
    private final MappackMetadata metadata;
    private final StatConfig statConfig = new jk_5.nailed.map.stat.StatConfig();
    public final MappackFilestore filestore = new MappackFilestore();

    public IpcMappack(JsonObject json){
        this.id = json.get("mpid").getAsString();
        this.metadata = new JsonMappackMetadata(json);
        this.filestore.files = gson.fromJson(json.get("worldFiles"), new TypeToken<List<MappackFile>>(){}.getType());
    }

    @Override
    public String getMappackID(){
        return this.id;
    }

    @Override
    public MappackMetadata getMappackMetadata(){
        return this.metadata;
    }

    @Override
    public StatConfig getStatConfig(){
        return this.statConfig;
    }

    @Override
    public void prepareWorld(final File destinationDir, final Callback<Void> callback){
        this.filestore.requestMissingFiles(new Callback<Void>(){
            @Override
            public void callback(Void obj) {
                filestore.reconstruct(destinationDir);
                callback.callback(null);
            }
        });
    }

    @Override
    public Map createMap(MapBuilder mapBuilder){
        return mapBuilder.build();
    }

    @Override
    public boolean saveAsMappack(Map map){
        return false;
    }

    @Override
    public ByteBuf getMappackIcon(){
        return null;
    }

    @Override
    public IMount createMount(){
        return null;
    }
}

package jk_5.nailed.ipc.mappack;

import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.MapBuilder;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.map.stat.StatConfig;
import jk_5.nailed.api.scripting.IMount;
import jk_5.nailed.map.mappack.JsonMappackMetadata;

import java.io.File;

/**
 * No description given
 *
 * @author jk-5
 */
public class IpcMappack implements Mappack {

    private final String id;
    private final MappackMetadata metadata;
    private final StatConfig statConfig = new jk_5.nailed.map.stat.StatConfig();

    public IpcMappack(JsonObject json){
        this.id = json.get("mpid").getAsString();
        this.metadata = new JsonMappackMetadata(json);
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
    public void prepareWorld(File destinationDir, Callback<Void> callback){
        //TODO: retrieve the mappack from the server in here
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

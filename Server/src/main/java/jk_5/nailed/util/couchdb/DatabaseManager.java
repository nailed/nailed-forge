package jk_5.nailed.util.couchdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ning.http.client.*;
import io.netty.handler.codec.http.HttpHeaders;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.database.DataOwner;
import jk_5.nailed.players.NailedPlayer;
import jk_5.nailed.util.config.ConfigTag;
import lombok.Getter;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * No description given
 *
 * @author jk-5
 */
public class DatabaseManager {

    @Getter private static final DatabaseManager instance = new DatabaseManager();

    private boolean enabled;
    private boolean ssl;
    private String host;
    private int port;
    private String database;

    private static final AsyncHttpClient httpClient = new AsyncHttpClient();
    private static final Executor executor = Executors.newCachedThreadPool();
    private static final JsonParser jsonParser = new JsonParser();
    private static final Gson gson = new Gson();

    public void readConfig(ConfigTag config){
        config.setComment("Nailed stores game results and player data in a couchdb database. These are the settings");
        config.useBraces();
        this.enabled = config.getTag("enabled").getBooleanValue(false);
        this.host = config.getTag("host").getValue("localhost");
        this.port = config.getTag("port").getIntValue(5984);
        this.database = config.getTag("name").getValue("nailed-forge");
        this.ssl = config.getTag("useSsl").getBooleanValue(false);
    }

    public void init(){
        if(!this.enabled){
            return;
        }
        //TODO
    }

    private String getUrl(){
        return (this.ssl ? "https://" : "http://") + this.host + ":" + this.port + "/" + this.database + "/";
    }

    public ListenableFuture<Response> getDocument(String id){
        try{
            RequestBuilder builder = new RequestBuilder("GET");
            builder.setUrl(this.getUrl() + id);
            Request request = builder.build();
            return httpClient.executeRequest(request);
        }catch(IOException e){
            NailedLog.error("Error while executing http request", e);
        }
        return null;
    }

    public ListenableFuture<Response> updateDocument(String id, JsonObject data){
        try{
            RequestBuilder builder = new RequestBuilder("PUT");
            builder.setUrl(this.getUrl() + id);
            builder.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json");
            builder.setBody(gson.toJson(data));
            Request request = builder.build();
            return httpClient.executeRequest(request);
        }catch(IOException e){
            NailedLog.error("Error while executing http request", e);
        }
        return null;
    }

    public static void saveData(DataOwner dataOwner){
        JsonObject data = new JsonObject();
        dataOwner.getData().write(data);
        data.addProperty("_id", dataOwner.getId());
        getInstance().updateDocument(dataOwner.getId(), data);
    }

    public static void loadData(final DataOwner dataOwner){
        final ListenableFuture<Response> future = getInstance().getDocument(dataOwner.getId());
        future.addListener(new Runnable() {
            @Override
            public void run(){
                try{
                    Response response = future.get();
                    JsonObject obj = jsonParser.parse(response.getResponseBody()).getAsJsonObject();
                    dataOwner.getData().read(obj);
                    if(dataOwner instanceof NailedPlayer){
                        ((NailedPlayer) dataOwner).onDataLoaded();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, executor);
    }
}

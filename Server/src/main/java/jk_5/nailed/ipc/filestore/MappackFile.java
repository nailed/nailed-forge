package jk_5.nailed.ipc.filestore;

import java.io.*;
import javax.annotation.*;

import com.ning.http.client.*;

import org.apache.commons.io.*;
import org.apache.logging.log4j.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.concurrent.*;
import jk_5.nailed.ipc.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class MappackFile {

    private static final Logger logger = LogManager.getLogger();

    public String path;
    public String hash;
    public long size;
    private File location;

    public boolean isAvailable() {
        File loc = this.getLocation();
        return loc.isFile();
    }

    public File getLocation() {
        if(this.location != null){
            return this.location;
        }
        File loc = new File(MappackFilestore.objects, this.hash.substring(0, 2));
        loc = new File(loc, this.hash);
        this.location = loc;
        return loc;
    }

    public void download(@Nullable final Callback<MappackFile> callback) {
        try{
            final File dest = new File(new File(MappackFilestore.objects, this.hash.substring(0, 2)), this.hash);
            dest.getParentFile().mkdirs();
            Request req = MappackFilestore.httpClient.prepareGet("http://" + IpcManager.instance().getHost() + ":" + IpcManager.instance().getPort() + "/api/data/" + this.hash + "/").build();
            final ListenableFuture<Response> future = MappackFilestore.httpClient.executeRequest(req);
            future.addListener(new Runnable() {
                @Override
                public void run() {
                    FileOutputStream fos = null;
                    try{
                        Response resp = future.get();
                        if(resp.getStatusCode() == 200){
                            fos = new FileOutputStream(dest);
                            fos.write(resp.getResponseBodyAsBytes());
                            logger.info("Successfully downloaded and written mappack file (Hash: {})", hash);
                            if(callback != null){
                                callback.callback(MappackFile.this);
                            }
                        }else{
                            logger.warn("Got non-200 response code while downloading mappack data ({})", resp.getStatusCode());
                        }
                    }catch(Exception e){
                        logger.warn("Error while processing downloaded mappack data", e);
                    }finally{
                        IOUtils.closeQuietly(fos);
                    }
                }
            }, NailedAPI.getScheduler());
        }catch(Exception e){
            logger.warn("Error while downloading mappack data", e);
        }
    }
}

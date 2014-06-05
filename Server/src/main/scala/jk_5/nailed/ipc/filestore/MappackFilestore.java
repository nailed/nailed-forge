package jk_5.nailed.ipc.filestore;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

import org.apache.commons.io.FileUtils;

import jk_5.nailed.api.concurrent.Callback;

/**
 * No description given
 *
 * @author jk-5
 */
public class MappackFilestore {

    public static final File storage = new File("mappacks");
    public static final File objects = new File(storage, "objects");
    public static final AsyncHttpClient httpClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().setConnectionTimeoutInMs(10000).setRequestTimeoutInMs(10000).build());

    public List<MappackFile> files = Lists.newArrayList();
    public Map<String, MappackFile> paths = Maps.newHashMap();

    public void requestMissingFiles(final Callback<Void> callback) {
        final Set<MappackFile> downloading = Sets.newHashSet();
        for(MappackFile file : files){
            if(!file.isAvailable()){
                downloading.add(file);
            }
        }

        if(downloading.size() == 0){
            if(callback != null){
                callback.callback(null);
            }
        }else{
            Callback<MappackFile> loadCB = new Callback<MappackFile>() {
                private final AtomicInteger downloadsLeft = new AtomicInteger(downloading.size());

                @Override
                public void callback(MappackFile obj) {
                    if(downloadsLeft.decrementAndGet() == 0){
                        if(callback != null){
                            callback.callback(null);
                        }
                    }
                }
            };

            for(MappackFile file : downloading){
                file.download(loadCB);
            }
        }
    }

    public void reconstruct(File destinationDir) {
        destinationDir.mkdirs();
        for(MappackFile file : this.files){
            File dest = new File(destinationDir, file.path);
            dest.getParentFile().mkdirs();
            try{
                FileUtils.copyFile(file.getLocation(), dest);
            }catch(IOException ignored){
            }
        }
    }

    public void refresh() {
        this.paths.clear();
        for(MappackFile file : this.files){
            this.paths.put(file.path, file);
        }
    }
}

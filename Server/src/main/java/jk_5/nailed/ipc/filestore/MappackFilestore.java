package jk_5.nailed.ipc.filestore;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ning.http.client.AsyncHttpClient;
import jk_5.nailed.api.concurrent.Callback;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * No description given
 *
 * @author jk-5
 */
public class MappackFilestore {

    public static final File storage = new File("mappacks");
    public static final File objects = new File(storage, "objects");
    public static final AsyncHttpClient httpClient = new AsyncHttpClient();

    public List<MappackFile> files = Lists.newArrayList();

    public void requestMissingFiles(final Callback<Void> callback){
        final Set<MappackFile> downloading = Sets.newHashSet();
        for(MappackFile file : files){
            if(!file.isAvailable()){
                downloading.add(file);
            }
        }

        if(downloading.size() == 0){
            if(callback != null) callback.callback(null);
        }else{
            Callback<MappackFile> loadCB = new Callback<MappackFile>() {
                private final AtomicInteger downloadsLeft = new AtomicInteger(downloading.size());
                @Override
                public void callback(MappackFile obj) {
                    if(downloadsLeft.decrementAndGet() == 0){
                        if(callback != null) callback.callback(null);
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
            }catch(IOException ignored){}
        }
    }
}
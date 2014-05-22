package jk_5.nailed.map.mappack;

import java.io.*;
import java.util.*;
import javax.annotation.*;

import com.google.common.collect.*;

import jk_5.nailed.*;
import jk_5.nailed.api.*;
import jk_5.nailed.api.concurrent.*;
import jk_5.nailed.api.concurrent.scheduler.*;
import jk_5.nailed.api.map.*;
import jk_5.nailed.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedMappackLoader implements MappackLoader {

    public boolean loadASync = false;

    private final File mappackFolder = new File("mappacks");
    private final List<Mappack> mappacks = Lists.newArrayList();
    private final List<MappackReloadListener> listeners = Lists.newArrayList();

    @Override
    @Nullable
    public Mappack getMappack(@Nonnull String mappackID) {
        for(Mappack pack : this.mappacks){
            if(pack.getMappackID().equals(mappackID)){
                return pack;
            }
        }
        return null;
    }

    @Override
    public void loadMappacks(@Nullable final Callback<MappackLoader> callback) {
        NailedRunnable runnable = new NailedRunnable() {
            @Override
            public void run() {
                NailedLog.info("Loading mappacks...");
                if(!mappackFolder.exists()){
                    mappackFolder.mkdirs();
                }
                File[] list = mappackFolder.listFiles();
                if(list == null){
                    return;
                }
                List<Mappack> newMappackList = Lists.newArrayList();
                for(File file : list){
                    try{
                        if(file.isFile() && (file.getName().endsWith(".zip") || file.getName().endsWith(".mappack"))){
                            newMappackList.add(ZipMappack.create(file));
                            NailedLog.info("Successfully loaded mappack {}", file.getName());
                        }else if(file.isDirectory()){
                            newMappackList.add(DirectoryMappack.create(file));
                            NailedLog.info("Successfully loaded mappack {}", file.getName());
                        }
                    }catch(DiscardedMappackInitializationException e){
                        //Discard!
                        NailedLog.warn("An error was thrown while loading mappack {}, skipping it!", file.getName());
                    }catch(MappackInitializationException e){
                        NailedLog.error("Error while loading mappack {}, skipping it!", file.getName());
                        NailedLog.error("Exception: ", e);
                    }
                }
                NailedMappackLoader.this.mappacks.clear();
                NailedMappackLoader.this.mappacks.addAll(newMappackList);
                for(MappackReloadListener listener : NailedMappackLoader.this.listeners){
                    listener.onReload(NailedMappackLoader.this);
                }
                NailedLog.info("Successfully loaded {} mappacks!", newMappackList.size());
                if(callback != null){
                    callback.callback(NailedMappackLoader.this);
                }
            }
        };
        if(this.loadASync){
            NailedAPI.getScheduler().runTaskAsynchronously(runnable);
        }else{
            runnable.run();
        }
    }

    @Override
    public void registerMappack(@Nonnull Mappack mappack) {
        this.mappacks.add(mappack);
    }

    @Override
    public void registerReloadListener(@Nonnull MappackReloadListener listener) {
        this.listeners.add(listener);
    }

    @Override
    @Nonnull
    public File getMappackFolder() {
        return mappackFolder;
    }

    @Override
    @Nonnull
    public List<Mappack> getMappacks() {
        return mappacks;
    }
}

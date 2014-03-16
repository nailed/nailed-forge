package jk_5.nailed.map.mappack;

import com.google.common.collect.Lists;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.concurrent.scheduler.NailedRunnable;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.MappackLoader;
import jk_5.nailed.map.DiscardedMappackInitializationException;
import jk_5.nailed.map.MappackInitializationException;
import lombok.Getter;

import java.io.File;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedMappackLoader implements MappackLoader {

    @Getter private final File mappackFolder = new File("mappacks");
    @Getter private final List<Mappack> mappacks = Lists.newArrayList();

    @Override
    public Mappack getMappack(String mappackID){
        for(Mappack pack : this.mappacks){
            if(pack.getMappackID().equals(mappackID)){
                return pack;
            }
        }
        return null;
    }

    @Override
    public void loadMappacks(){
        NailedAPI.getScheduler().runTaskAsynchronously(new NailedRunnable() {
            @Override
            public void run(){
                NailedLog.info("Loading mappacks...");
                if(!mappackFolder.exists()) mappackFolder.mkdirs();
                File[] list = mappackFolder.listFiles();
                if(list == null) return;
                List<Mappack> newMappackList = Lists.newArrayList();
                for(File file : list){
                    try{
                        if(file.isFile() && (file.getName().endsWith(".zip") || file.getName().endsWith(".mappack"))){
                            newMappackList.add(ZipMappack.create(file));
                            NailedLog.info("Successfully loaded mappack " + file.getName());
                        }else if(file.isDirectory()){
                            newMappackList.add(DirectoryMappack.create(file));
                            NailedLog.info("Successfully loaded mappack " + file.getName());
                        }
                    }catch (DiscardedMappackInitializationException e){
                        //Discard!
                        NailedLog.warn("An error was thrown while loading mappack " + file.getName() + ", skipping it!");
                    }catch (MappackInitializationException e){
                        NailedLog.error(e, "Error while loading mappack " + file.getName() + ", skipping it!");
                    }
                }
                NailedMappackLoader.this.mappacks.clear();
                NailedMappackLoader.this.mappacks.addAll(newMappackList);
                NailedLog.info("Successfully loaded %d mappacks!", newMappackList.size());
            }
        });
    }

    @Override
    public void registerMappack(Mappack mappack){
        this.mappacks.add(mappack);
    }
}

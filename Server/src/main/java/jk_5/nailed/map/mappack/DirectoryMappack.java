package jk_5.nailed.map.mappack;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.annotation.*;

import com.google.gson.*;

import org.apache.commons.io.*;

import net.minecraft.server.*;
import net.minecraft.world.*;

import jk_5.nailed.api.concurrent.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.scripting.*;
import jk_5.nailed.api.zone.*;
import jk_5.nailed.map.*;
import jk_5.nailed.map.script.*;
import jk_5.nailed.map.stat.*;
import jk_5.nailed.permissions.zone.*;

/**
 * No description given
 *
 * @author jk-5
 */
public final class DirectoryMappack implements Mappack {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");

    private final String mappackID;
    private final File mappackFolder;
    private final MappackMetadata mappackMetadata;
    private StatConfig statConfig = new StatConfig();
    private ZoneConfig zoneConfig = new DefaultZoneConfig();

    private DirectoryMappack(File directory, JsonMappackMetadata metadata) {
        this.mappackID = directory.getName();
        this.mappackFolder = directory;
        this.mappackMetadata = metadata;
        if(metadata.name == null){
            metadata.name = this.mappackID;
        }
    }

    public static DirectoryMappack create(File directory) throws MappackInitializationException {
        DirectoryMappack pack;
        StatConfig statConfig = new StatConfig();
        ZoneConfig zoneConfig = new DefaultZoneConfig();
        File mappackConfig = new File(directory, "mappack.json");
        File statConfigFile = new File(directory, "stats.json");
        File zoneConfigFile = new File(directory, "zones.json");
        if(mappackConfig.isFile() && mappackConfig.exists()){
            FileReader fr = null;
            try{
                fr = new FileReader(mappackConfig);
                pack = new DirectoryMappack(directory, new JsonMappackMetadata((JsonObject) new JsonParser().parse(fr)));
            }catch(Exception e){
                throw new MappackInitializationException("Exception while reading mappack.json", e);
            }finally{
                IOUtils.closeQuietly(fr);
            }
        }else{
            throw new DiscardedMappackInitializationException("Directory " + directory.getPath() + " is not a mappack");
        }
        if(statConfigFile.isFile() && statConfigFile.exists()){
            FileReader fr = null;
            try{
                fr = new FileReader(statConfigFile);
                statConfig = new StatConfig(new JsonParser().parse(fr).getAsJsonArray());
            }catch(Exception e){
                throw new MappackInitializationException("Exception while reading stats.json", e);
            }finally{
                IOUtils.closeQuietly(fr);
            }
        }
        if(zoneConfigFile.isFile() && zoneConfigFile.exists()){
            FileReader fr = null;
            try{
                fr = new FileReader(zoneConfigFile);
                zoneConfig = new DefaultZoneConfig(new JsonParser().parse(fr).getAsJsonArray());
            }catch(Exception e){
                throw new MappackInitializationException("Exception while reading zones.json", e);
            }finally{
                IOUtils.closeQuietly(fr);
            }
        }
        pack.statConfig = statConfig;
        pack.zoneConfig = zoneConfig;
        return pack;
    }

    @Override
    public void prepareWorld(@Nonnull File destinationDir, @Nullable Callback<Void> callback) {
        File world = new File(this.mappackFolder, "world");
        if(world.isDirectory() && world.exists()){
            try{
                FileUtils.copyDirectory(world, destinationDir);
            }catch(IOException e){
                throw new RuntimeException("Error while preparing mappack", e);
            }
        }
        if(callback != null){
            callback.callback(null);
        }
    }

    @Override
    @Nonnull
    public Map createMap(@Nonnull MapBuilder builder) {
        return builder.build();
    }

    @Override
    public boolean saveAsMappack(@Nonnull Map map) {
        File worldDir = new File(this.mappackFolder, "world");
        if(worldDir.isDirectory() && worldDir.exists()){
            worldDir.renameTo(new File(this.mappackFolder, "world-backup-" + dateFormat.format(new Date())));
        }

        MinecraftServer server = MinecraftServer.getServer();

        if(server.getConfigurationManager() != null){
            server.getConfigurationManager().saveAllPlayerData();
        }

        try{
            WorldServer world = map.getWorld();
            boolean notSaveEnabled = world.levelSaving;
            world.levelSaving = false;
            world.saveAllChunks(true, null);

            world.levelSaving = true;

            FileUtils.copyDirectory(map.getSaveFolder(), worldDir, new FileFilter() {
                @Override
                public boolean accept(File file) {
                    File parent = file.getParentFile();
                    return file.getName().startsWith("level.dat") || ("region".equals(file.getName()) && file.isDirectory()) || ("region".equals(parent.getName()) && parent.isDirectory() && file.getName().endsWith(".mca"));
                }
            });

            world.levelSaving = notSaveEnabled;
        }catch(MinecraftException e){
            throw new RuntimeException("Save failed", e);
        }catch(IOException e){
            throw new RuntimeException("Save failed", e);
        }

        return true;
    }

    @Override
    @Nullable
    public IMount createMount() {
        return new ReadOnlyMount(new File(this.mappackFolder, "lua"));
    }

    @Nonnull
    @Override
    public String getMappackID() {
        return this.mappackID;
    }

    @Nonnull
    @Override
    public jk_5.nailed.api.map.stat.StatConfig getStatConfig() {
        return this.statConfig;
    }

    @Nonnull
    @Override
    public MappackMetadata getMappackMetadata() {
        return this.mappackMetadata;
    }

    @Nonnull
    @Override
    public ZoneConfig getZoneConfig() {
        return this.zoneConfig;
    }
}

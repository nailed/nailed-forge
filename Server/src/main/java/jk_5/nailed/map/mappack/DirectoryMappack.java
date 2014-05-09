package jk_5.nailed.map.mappack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.MapBuilder;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.scripting.IMount;
import jk_5.nailed.api.zone.ZoneConfig;
import jk_5.nailed.map.DiscardedMappackInitializationException;
import jk_5.nailed.map.MappackInitializationException;
import jk_5.nailed.map.script.ReadOnlyMount;
import jk_5.nailed.map.stat.StatConfig;
import jk_5.nailed.permissions.zone.DefaultZoneConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * No description given
 *
 * @author jk-5
 */
public class DirectoryMappack implements Mappack {

    private final String mappackID;
    private final File mappackFolder;
    private final MappackMetadata mappackMetadata;
    private StatConfig statConfig = new StatConfig();
    private ZoneConfig zoneConfig = new DefaultZoneConfig();

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");

    private DirectoryMappack(File directory, JsonMappackMetadata metadata){
        this.mappackID = directory.getName();
        this.mappackFolder = directory;
        this.mappackMetadata = metadata;
        if(metadata.name == null) metadata.name = this.mappackID;
    }

    public static DirectoryMappack create(File directory) throws MappackInitializationException{
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
    public void prepareWorld(@Nonnull File destinationDir, @Nullable Callback<Void> callback){
        File world = new File(this.mappackFolder, "world");
        if(world.isDirectory() && world.exists()){
            try{
                FileUtils.copyDirectory(world, destinationDir);
            }catch(IOException e){
                throw new RuntimeException("Error while preparing mappack", e);
            }
        }
        if(callback != null) callback.callback(null);
    }

    @Override
    @Nonnull
    public Map createMap(@Nonnull MapBuilder builder){
        return builder.build();
    }

    @Override
    public boolean saveAsMappack(@Nonnull Map map){
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
                    return file.getName().startsWith("level.dat") || (file.getName().equals("region") && file.isDirectory()) || (parent.getName().equals("region") && parent.isDirectory() && file.getName().endsWith(".mca"));
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
    public IMount createMount(){
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

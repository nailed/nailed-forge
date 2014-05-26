package jk_5.nailed.map.mappack;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.annotation.*;

import com.google.gson.*;

import org.apache.commons.io.*;

import jk_5.nailed.*;
import jk_5.nailed.api.concurrent.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.scripting.*;
import jk_5.nailed.api.zone.*;
import jk_5.nailed.map.*;
import jk_5.nailed.map.stat.*;
import jk_5.nailed.permissions.zone.*;

/**
 * No description given
 *
 * @author jk-5
 */
public final class ZipMappack implements Mappack {

    private final String mappackID;
    private final File mappackFile;
    private final MappackMetadata mappackMetadata;
    private StatConfig statConfig;
    private ZoneConfig zoneConfig;

    private ZipMappack(File mappackFile, JsonMappackMetadata metadata) {
        this.mappackID = mappackFile.getName().substring(0, mappackFile.getName().length() - 8);
        this.mappackFile = mappackFile;
        this.mappackMetadata = metadata;
        if(metadata.name == null){
            metadata.name = this.mappackID;
        }
    }

    public static Mappack create(File file) throws MappackInitializationException {
        ZipMappack pack = null;
        StatConfig stats = new StatConfig();
        ZoneConfig zones = new DefaultZoneConfig();
        ZipInputStream zipStream = null;
        try{
            zipStream = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry = zipStream.getNextEntry();
            while(entry != null){
                if("mappack.json".equals(entry.getName())){
                    pack = new ZipMappack(file, new JsonMappackMetadata((JsonObject) new JsonParser().parse(new InputStreamReader(zipStream))));
                }else if("stats.json".equals(entry.getName())){
                    stats = new StatConfig(new JsonParser().parse(new InputStreamReader(zipStream)).getAsJsonArray());
                }else if("zones.json".equals(entry.getName())){
                    zones = new DefaultZoneConfig(new JsonParser().parse(new InputStreamReader(zipStream)).getAsJsonArray());
                }
                entry = zipStream.getNextEntry();
            }
        }catch(FileNotFoundException e){
            NailedLog.error("Discovered mappack file {} is gone now? This is impossible", file.getPath());
            NailedLog.error("Exception: ", e);
            throw new DiscardedMappackInitializationException("Mappack file " + file.getPath() + " disappeared!", e);
        }catch(IOException e){
            throw new MappackInitializationException("Mappack file " + file.getPath() + " could not be read", e);
        }finally{
            IOUtils.closeQuietly(zipStream);
        }
        if(pack == null){
            throw new DiscardedMappackInitializationException("mappack.json was not found in mappack " + file.getName());
        }
        pack.statConfig = stats;
        pack.zoneConfig = zones;
        return pack;
    }

    @Override
    public void prepareWorld(@Nonnull File destinationDir, @Nullable Callback<Void> callback) {
        this.unzipMapFromMapPack(this.mappackFile, destinationDir);
        if(callback != null){
            callback.callback(null);
        }
    }

    @Override
    @Nonnull
    public Map createMap(@Nonnull MapBuilder potentialMap) {
        return potentialMap.build();
    }

    @Override
    public boolean saveAsMappack(@Nonnull Map map) {
        return false;
    }

    public File unzipMapFromMapPack(File mapPack, File destDir) {
        try{
            ZipFile zipFile = new ZipFile(mapPack);
            Enumeration e = zipFile.entries();
            File worldDir = null;
            while(e.hasMoreElements()){
                ZipEntry entry = (ZipEntry) e.nextElement();
                if("mappack.json".equals(entry.getName())){
                    continue;
                }
                if("gameinstructions.cfg".equals(entry.getName())){
                    continue;
                }
                if(entry.getName().contains("##MCEDIT.TEMP##")){
                    continue;
                }
                if(entry.getName().startsWith("__MACOSX/")){
                    continue;
                }
                File destinationFilePath = new File(destDir.getParentFile(), entry.getName());
                destinationFilePath.getParentFile().mkdirs();
                if(!entry.isDirectory()){
                    BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destinationFilePath), 1024);
                    IOUtils.copy(bis, bos);
                    bos.flush();
                    bos.close();
                    bis.close();
                }else if("world/".equals(entry.getName())){
                    worldDir = destinationFilePath;
                }
            }
            if(worldDir == null){
                throw new RuntimeException("Invalid or corrupt mappack file");
            }
            worldDir.renameTo(destDir);
            return destDir;
        }catch(IOException ioe){
            NailedLog.error("Error while unpacking file", ioe);
        }
        return null;
    }

    @Override
    @Nullable
    public IMount createMount() {
        return null;
    }

    @Nonnull
    @Override
    public String getMappackID() {
        return this.mappackID;
    }

    @Nonnull
    @Override
    public MappackMetadata getMappackMetadata() {
        return this.mappackMetadata;
    }

    @Nonnull
    @Override
    public jk_5.nailed.api.map.stat.StatConfig getStatConfig() {
        return this.statConfig;
    }

    @Nonnull
    @Override
    public ZoneConfig getZoneConfig() {
        return this.zoneConfig;
    }
}

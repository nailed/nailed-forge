package jk_5.nailed.map.mappack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.MapBuilder;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.scripting.IMount;
import jk_5.nailed.map.DiscardedMappackInitializationException;
import jk_5.nailed.map.MappackInitializationException;
import jk_5.nailed.map.stat.StatConfig;
import jk_5.nailed.util.config.ConfigFile;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * No description given
 *
 * @author jk-5
 */
public class ZipMappack implements Mappack {

    private final String mappackID;
    private final String iconFile;
    private final File mappackFile;
    private final MappackMetadata mappackMetadata;
    private StatConfig statConfig;

    private ZipMappack(File mappackFile, ConfigFile config){
        this.mappackID = mappackFile.getName().substring(0, mappackFile.getName().length() - 8);
        this.iconFile = config.getTag("map").getTag("iconFile").getValue("icon.png");
        this.mappackFile = mappackFile;
        this.mappackMetadata = new FileMappackMetadata(config);
    }

    public static Mappack create(File file) throws MappackInitializationException {
        ZipMappack pack = null;
        ConfigFile config = null;
        StatConfig stats = new StatConfig();
        ZipInputStream zipStream = null;
        try{
            zipStream = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry = zipStream.getNextEntry();
            while(entry != null){
                if(entry.getName().equals("mappack.cfg")){
                    config = new ConfigFile(new InputStreamReader(zipStream)).setReadOnly();
                    pack = new ZipMappack(file, config);
                }else if(entry.getName().equals("stats.cfg")){
                    stats = new StatConfig(new ConfigFile(new InputStreamReader(zipStream)).setReadOnly());
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
        if(config == null){
            throw new DiscardedMappackInitializationException("mappack.cfg was not found in mappack " + file.getName());
        }
        pack.statConfig = stats;
        return pack;
    }

    @Override
    public void prepareWorld(@Nonnull File destinationDir, @Nonnull Callback<Void> callback) {
        this.unzipMapFromMapPack(this.mappackFile, destinationDir);
        callback.callback(null);
    }

    @Override
    @Nonnull
    public Map createMap(@Nonnull MapBuilder potentialMap) {
        return potentialMap.build();
    }

    @Override
    public boolean saveAsMappack(@Nonnull Map map){
        return false;
    }

    @Override
    @Nullable
    public ByteBuf getMappackIcon(){
        ByteBuf buf = Unpooled.buffer();
        ZipInputStream zipStream = null;
        try{
            zipStream = new ZipInputStream(new FileInputStream(new File(this.mappackFile, this.iconFile)));
            ZipEntry entry = zipStream.getNextEntry();
            while(entry != null){
                if(entry.getName().equals(this.iconFile)){
                    BufferedImage image = ImageIO.read(zipStream);
                    ImageIO.write(image, "PNG", new ByteBufOutputStream(buf));
                }
                entry = zipStream.getNextEntry();
            }
        }catch(IOException e){
            return null;
        }finally{
            IOUtils.closeQuietly(zipStream);
        }
        return buf;
    }

    public File unzipMapFromMapPack(File mapPack, File destDir){
        try{
            ZipFile zipFile = new ZipFile(mapPack);
            Enumeration e = zipFile.entries();
            File worldDir = null;
            while(e.hasMoreElements()){
                ZipEntry entry = (ZipEntry)e.nextElement();
                if(entry.getName().equals("mappack.cfg")) continue;
                if(entry.getName().equals("gameinstructions.cfg")) continue;
                if(entry.getName().contains("##MCEDIT.TEMP##")) continue;
                if(entry.getName().startsWith("__MACOSX/")) continue;
                File destinationFilePath = new File(destDir.getParentFile(), entry.getName());
                destinationFilePath.getParentFile().mkdirs();
                if(!entry.isDirectory()){
                    BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

                    int b;
                    byte buffer[] = new byte[1024];
                    FileOutputStream fos = new FileOutputStream(destinationFilePath);
                    BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
                    while ((b = bis.read(buffer, 0, 1024)) != -1) {
                        bos.write(buffer, 0, b);
                    }
                    bos.flush();
                    bos.close();
                    bis.close();
                }else if(entry.getName().equals("world/")){
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
    public IMount createMount(){
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
}

package jk_5.nailed.map.mappack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.config.ConfigFile;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.MapBuilder;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.scripting.IMount;
import jk_5.nailed.map.DiscardedMappackInitializationException;
import jk_5.nailed.map.MappackInitializationException;
import jk_5.nailed.map.instruction.InstructionList;
import jk_5.nailed.map.instruction.InstructionParseException;
import jk_5.nailed.map.stat.StatConfig;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;

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

    @Getter private final String mappackID;
    @Getter private final String name;
    @Getter private final String iconFile;
    @Getter private final File mappackFile;
    @Getter private final MappackMetadata mappackMetadata;
    @Getter @Setter private InstructionList instructionList;
    @Getter @Setter private StatConfig statConfig;

    private ZipMappack(File mappackFile, ConfigFile config){
        this.mappackID = mappackFile.getName().substring(0, mappackFile.getName().length() - 8);
        this.name = config.getTag("map").getTag("name").getValue(this.mappackID);
        this.iconFile = config.getTag("map").getTag("iconFile").getValue("icon.png");
        this.mappackFile = mappackFile;
        this.mappackMetadata = new FileMappackMetadata(config);
    }

    public static Mappack create(File file) throws MappackInitializationException {
        ZipMappack pack = null;
        ConfigFile config = null;
        StatConfig stats = new StatConfig();
        ZipInputStream zipStream = null;
        InstructionList instructionList = new InstructionList();
        try{
            zipStream = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry = zipStream.getNextEntry();
            while(entry != null){
                if(entry.getName().equals("mappack.cfg")){
                    config = new ConfigFile(new InputStreamReader(zipStream)).setReadOnly();
                    pack = new ZipMappack(file, config);
                }else if(entry.getName().equals("gameinstructions.cfg")){
                    instructionList = InstructionList.readFrom(new BufferedReader(new InputStreamReader(zipStream)));
                }else if(entry.getName().equals("stats.cfg")){
                    stats = new StatConfig(new ConfigFile(new InputStreamReader(zipStream)).setReadOnly());
                }
                entry = zipStream.getNextEntry();
            }
        }catch(InstructionParseException e){
            NailedLog.error("There was an error in your instruction syntax in " + file.getName());
            NailedLog.error(e.getMessage());
            throw new DiscardedMappackInitializationException("Instruction syntax error!", e);
        }catch(FileNotFoundException e){
            NailedLog.error(e, "Discovered mappack file " + file.getPath() + " is gone now? This is impossible");
            throw new DiscardedMappackInitializationException("Mappack file " + file.getPath() + " disappeared!", e);
        }catch(IOException e){
            throw new MappackInitializationException("Mappack file " + file.getPath() + " could not be read", e);
        }finally{
            IOUtils.closeQuietly(zipStream);
        }
        if(config == null){
            throw new DiscardedMappackInitializationException("mappack.cfg was not found in mappack " + file.getName());
        }
        pack.setInstructionList(instructionList);
        pack.setStatConfig(stats);
        return pack;
    }

    @Override
    public File prepareWorld(File destinationDir) {
        return this.unzipMapFromMapPack(this.mappackFile, destinationDir);
    }

    @Override
    public Map createMap(MapBuilder potentialMap) {
        return potentialMap.build();
    }

    @Override
    public boolean saveAsMappack(Map map){
        return false;
    }

    @Override
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
                System.err.println("Invalid or corrupt mappack file");
                System.exit(1);
            }
            worldDir.renameTo(destDir);
            return destDir;
        }catch(IOException ioe){
            NailedLog.error(ioe, "Error while unpacking file");
        }
        return null;
    }

    @Override
    public IMount createMount(){
        return null;
    }
}

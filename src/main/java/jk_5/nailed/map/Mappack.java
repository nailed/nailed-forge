package jk_5.nailed.map;

import jk_5.nailed.NailedLog;
import jk_5.nailed.map.instruction.IInstruction;
import jk_5.nailed.map.instruction.InstructionReader;
import jk_5.nailed.util.config.ConfigFile;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * No description given
 *
 * @author jk-5
 */
public class Mappack {

    private static AtomicInteger nextId = new AtomicInteger();

    public static Mappack create(File file) throws MappackInitializationException {
        Mappack pack = null;
        ConfigFile config = null;
        ZipInputStream zipStream = null;
        List<IInstruction> instructionList = null;
        try{
            zipStream = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry = zipStream.getNextEntry();
            while (entry != null){
                if(entry.getName().equals("mappack.cfg")){
                    config = new ConfigFile(new InputStreamReader(zipStream)).setReadOnly();
                    pack = new Mappack(file, config);
                }else if(entry.getName().equals("gameinstructions.cfg")){
                    try{
                        instructionList = InstructionReader.readInstructions(new BufferedReader(new InputStreamReader(zipStream)));
                    }catch(IOException e){
                        NailedLog.severe(e, "Error while reading instructions for " + entry.getName());
                        throw new MappackInitializationException(null, "Error while reading instructions!", e);
                    }
                }
                entry = zipStream.getNextEntry();
            }
        }catch(FileNotFoundException e){
            NailedLog.severe(e, "Discovered mappack file is gone now? This is impossible");
            throw new MappackInitializationException(null, "Mappack file disappeared!", e);
        } catch (IOException e) {
            throw new MappackInitializationException(null, "Mappack file could not be read", e);
        } finally {
            IOUtils.closeQuietly(zipStream);
        }
        if(config == null){
            throw new MappackInitializationException(null, "mappack.cfg was not found in mappack " + file.getName());
        }
        if(instructionList != null) pack.setInstructions(instructionList);
        return pack;
    }

    @Getter private final String internalName;
    @Getter private final String name;
    @Getter private final File mappackFile;
    @Getter private final int UID = nextId.getAndIncrement();
    @Getter private final MappackConfig mappackConfig;
    @Setter private List<IInstruction> instructions;

    private Mappack(File mappackFile, ConfigFile config){
        this.internalName = mappackFile.getName().substring(0, mappackFile.getName().length() - 8);
        this.name = config.getTag("map").getTag("name").getValue(this.internalName);
        this.mappackFile = mappackFile;
        this.mappackConfig = new MappackConfig(config);
    }

    public File unpack(File destinationDir){
        return MapLoader.instance().unzipMapFromMapPack(this.mappackFile, destinationDir);
    }

    public Map createMap(){
        Map map = new Map(this);
        this.unpack(new File(MapLoader.getMapsFolder(), map.getSaveFileName()));
        return map;
    }

    public TeleportOptions getEntryPoint(){
        return new TeleportOptions(this.mappackConfig.getSpawnPoint(), 0);
    }
}

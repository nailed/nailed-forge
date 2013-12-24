package jk_5.nailed.map.mappack;

import jk_5.nailed.NailedLog;
import jk_5.nailed.map.*;
import jk_5.nailed.map.instruction.InstructionList;
import jk_5.nailed.map.instruction.InstructionParseException;
import jk_5.nailed.map.stat.StatConfig;
import jk_5.nailed.util.config.ConfigFile;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * No description given
 *
 * @author jk-5
 */
public class ZipMappack implements Mappack {

    @Getter private final String mappackID;
    @Getter private final String name;
    @Getter private final File mappackFile;
    @Getter private final MappackMetadata mappackMetadata;
    @Getter @Setter private InstructionList instructionList;
    @Getter @Setter private StatConfig statConfig;

    private ZipMappack(File mappackFile, ConfigFile config){
        this.mappackID = mappackFile.getName().substring(0, mappackFile.getName().length() - 8);
        this.name = config.getTag("map").getTag("name").getValue(this.mappackID);
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
            NailedLog.severe("There was an error in your instruction syntax in " + file.getName());
            NailedLog.severe(e.getMessage());
            throw new DiscardedMappackInitializationException("Instruction syntax error!", e);
        }catch(FileNotFoundException e){
            NailedLog.severe(e, "Discovered mappack file " + file.getPath() + " is gone now? This is impossible");
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
        return MapLoader.instance().unzipMapFromMapPack(this.mappackFile, destinationDir);
    }

    @Override
    public Map createMap(PotentialMap potentialMap) {
        return potentialMap.createMap();
    }

    @Override
    public boolean saveAsMappack(Map map){
        return false;
    }
}

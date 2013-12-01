package jk_5.nailed.map.mappack;

import jk_5.nailed.NailedLog;
import jk_5.nailed.map.DiscardedMappackInitializationException;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MappackInitializationException;
import jk_5.nailed.map.PotentialMap;
import jk_5.nailed.map.instruction.InstructionList;
import jk_5.nailed.map.instruction.InstructionParseException;
import jk_5.nailed.util.config.ConfigFile;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class DirectoryMappack implements Mappack {

    @Getter private final String mappackID;
    @Getter private final String name;
    @Getter private final File mappackFolder;
    @Getter private final MappackMetadata mappackMetadata;
    @Getter @Setter private InstructionList instructionList;

    private DirectoryMappack(File directory, ConfigFile config){
        this.mappackID = directory.getName();
        this.name = config.getTag("map").getTag("name").getValue(this.mappackID);
        this.mappackFolder = directory;
        this.mappackMetadata = new FileMappackMetadata(config);
    }

    public static DirectoryMappack create(File directory) throws MappackInitializationException{
        DirectoryMappack pack;
        ConfigFile config = null;
        InstructionList instructionList = null;
        try{
            File mappackConfig = new File(directory, "mappack.cfg");
            File instructionFile = new File(directory, "gameinstructions.cfg");
            if(mappackConfig.isFile() && mappackConfig.exists()){
                config = new ConfigFile(mappackConfig).setReadOnly();
                pack = new DirectoryMappack(directory, config);
            }else throw new DiscardedMappackInitializationException("Directory " + directory.getPath() + " is not a mappack");
            if(instructionFile.isFile() && instructionFile.exists()){
                BufferedReader instructionReader = new BufferedReader(new InputStreamReader(new FileInputStream(instructionFile)));
                instructionList = InstructionList.readFrom(instructionReader);
                IOUtils.closeQuietly(instructionReader);
            }
        }catch(InstructionParseException e){
            NailedLog.severe("There was an error in your instruction syntax in " + directory.getName());
            NailedLog.severe(e.getMessage());
            throw new DiscardedMappackInitializationException("Instruction syntax error!", e);
        }catch(FileNotFoundException e){
            NailedLog.severe(e, "Discovered mappack directory is gone now? This is impossible");
            throw new MappackInitializationException("Mappack directory disappeared!", e);
        }
        if(instructionList != null) pack.setInstructionList(instructionList);
        return pack;
    }

    @Override
    public File prepareWorld(File destinationDir) {
        File world = new File(this.mappackFolder, "world");
        if(world.isDirectory() && world.exists()){
            try{
                FileUtils.copyDirectory(world, destinationDir);
                return destinationDir;
            }catch(IOException e){
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public Map createMap(PotentialMap potentialMap) {
        return potentialMap.createMap();
    }
}

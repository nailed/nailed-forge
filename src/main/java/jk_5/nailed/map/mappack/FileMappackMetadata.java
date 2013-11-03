package jk_5.nailed.map.mappack;

import jk_5.nailed.util.config.ConfigFile;
import lombok.Getter;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 */
public class FileMappackMetadata implements MappackMetadata {

    @Getter public String name;
    @Getter public ChunkCoordinates spawnPoint;

    public FileMappackMetadata(ConfigFile config){
        int spawnX = config.getTag("spawnpoint").getTag("x").getIntValue(0);
        int spawnY = config.getTag("spawnpoint").getTag("y").getIntValue(64);
        int spawnZ = config.getTag("spawnpoint").getTag("z").getIntValue(0);
        this.name = config.getTag("map").getTag("name").getValue("");
        this.spawnPoint = new ChunkCoordinates(spawnX, spawnY, spawnZ);
    }
}

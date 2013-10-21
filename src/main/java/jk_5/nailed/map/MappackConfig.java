package jk_5.nailed.map;

import jk_5.nailed.util.config.ConfigFile;
import lombok.Data;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 */
@Data
public class MappackConfig {

    private final ConfigFile config;

    public final int spawnX;
    public final int spawnY;
    public final int spawnZ;
    public final ChunkCoordinates spawnPoint;

    public MappackConfig(ConfigFile config){
        this.config = config;
        this.spawnX = config.getTag("spawnpoint").getTag("x").getIntValue(0);
        this.spawnY = config.getTag("spawnpoint").getTag("y").getIntValue(64);
        this.spawnZ = config.getTag("spawnpoint").getTag("z").getIntValue(0);
        this.spawnPoint = new ChunkCoordinates(this.spawnX, this.spawnY, this.spawnZ);
    }
}

package jk_5.nailed.map.mappack;

import lombok.Getter;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 */
public class DefaultMappackMetadata implements MappackMetadata {

    @Getter public String name;
    @Getter public ChunkCoordinates spawnPoint;
}

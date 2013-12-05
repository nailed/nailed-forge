package jk_5.nailed.map.mappack;

import jk_5.nailed.players.TeamBuilder;
import lombok.Getter;
import net.minecraft.util.ChunkCoordinates;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class DefaultMappackMetadata implements MappackMetadata {

    @Getter public String name;
    @Getter public ChunkCoordinates spawnPoint;
    @Getter public List<TeamBuilder> defaultTeams;
}

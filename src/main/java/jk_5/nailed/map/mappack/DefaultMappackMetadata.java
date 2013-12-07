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
@Getter
public class DefaultMappackMetadata implements MappackMetadata {

    public String name;
    public ChunkCoordinates spawnPoint;
    public List<TeamBuilder> defaultTeams;
    public boolean spawnFriendlyMobs;
    public boolean spawnHostileMobs;
}

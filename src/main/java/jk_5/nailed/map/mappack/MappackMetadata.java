package jk_5.nailed.map.mappack;

import jk_5.nailed.players.TeamBuilder;
import net.minecraft.util.ChunkCoordinates;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public interface MappackMetadata {

    String getName();
    ChunkCoordinates getSpawnPoint();
    List<TeamBuilder> getDefaultTeams();
    boolean isSpawnHostileMobs();
    boolean isSpawnFriendlyMobs();
}

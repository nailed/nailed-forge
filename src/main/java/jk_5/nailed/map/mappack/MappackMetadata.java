package jk_5.nailed.map.mappack;

import jk_5.nailed.players.TeamBuilder;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.EnumGameType;

import java.util.List;
import java.util.Map;

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
    Map<String, String> getGameruleConfig();
    int getDifficulty();
    String getGameType();
    boolean isPreventingBlockBreak();
    float getSpawnYaw();
    float getSpawnPitch();
    boolean isPvpEnabled();
    EnumGameType getGamemode();
    boolean isChoosingRandomSpawnpointAtRespawn();
    List<Spawnpoint> getRandomSpawnpoints();
    String getStartWhen();
}

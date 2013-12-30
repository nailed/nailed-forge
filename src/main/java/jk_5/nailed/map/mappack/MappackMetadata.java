package jk_5.nailed.map.mappack;

import jk_5.nailed.players.TeamBuilder;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;

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
    EnumDifficulty getDifficulty();
    String getGameType();
    boolean isPreventingBlockBreak();
    float getSpawnYaw();
    float getSpawnPitch();
    boolean isPvpEnabled();
    WorldSettings.GameType getGamemode();
    boolean isChoosingRandomSpawnpointAtRespawn();
    List<Spawnpoint> getRandomSpawnpoints();
    String getStartWhen();
}
